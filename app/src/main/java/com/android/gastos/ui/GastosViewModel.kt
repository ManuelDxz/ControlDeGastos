package com.android.gastos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.gastos.data.CategoryEntity
import com.android.gastos.data.Expense
import com.android.gastos.data.GastosRepository
import com.android.gastos.data.IncomeEntry
import com.android.gastos.data.Period
import com.android.gastos.data.PeriodType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class PeriodSummary(
    val period: Period,
    val expenses: List<Expense>,
    val extraIncome: List<IncomeEntry>,
    val totalPaid: Double,
    val totalPending: Double,
    val totalExtraIncome: Double,
    val remaining: Double
)

data class MonthStudy(
    val yearMonth: YearMonth,
    val total: Double,
    val byCategory: List<Pair<CategoryEntity, Double>>
) {
    val topCategory: Pair<CategoryEntity, Double>? get() = byCategory.maxByOrNull { it.second }
}

class GastosViewModel(private val repository: GastosRepository) : ViewModel() {

    private val _selectedPeriodId = MutableStateFlow<Long?>(null)

    val periods: StateFlow<List<Period>> = repository.getAllPeriods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        periods.onEach { list ->
            val currentSelection = _selectedPeriodId.value
            if (list.none { it.id == currentSelection }) {
                _selectedPeriodId.value = list.firstOrNull()?.id
            }
        }.launchIn(viewModelScope)
    }

    val selectedPeriodId: StateFlow<Long?> = _selectedPeriodId

    val currentPeriod: StateFlow<Period?> = combine(periods, _selectedPeriodId) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val expensesForSelected: StateFlow<List<Expense>> = _selectedPeriodId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.getExpensesForPeriod(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val incomeForSelected: StateFlow<List<IncomeEntry>> = _selectedPeriodId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.getIncomeForPeriod(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val periodSummary: StateFlow<PeriodSummary?> = combine(currentPeriod, expensesForSelected, incomeForSelected) { period, expenses, extraIncome ->
        if (period == null) null
        else {
            val paid = expenses.filter { it.isPaid }.sumOf { it.amount }
            val pending = expenses.filter { !it.isPaid }.sumOf { it.amount }
            val extra = extraIncome.sumOf { it.amount }
            PeriodSummary(period, expenses, extraIncome, paid, pending, extra, period.income + extra - paid)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allExpenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncome: StateFlow<List<IncomeEntry>> = repository.getAllIncome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Totals by category across every paid expense, ranked descending — feeds the stats donut/bar chart. */
    val categoryTotals: StateFlow<List<Pair<CategoryEntity, Double>>> = combine(allExpenses, categories) { expenses, cats ->
        val byId = cats.associateBy { it.id }
        expenses.filter { it.isPaid }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, exps) -> byId[categoryId]?.let { it to exps.sumOf { e -> e.amount } } }
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Per-calendar-month breakdown (by the expense's own date, independent of which period it belongs to). */
    val monthlyStudy: StateFlow<List<MonthStudy>> = combine(allExpenses, categories) { expenses, cats ->
        val byId = cats.associateBy { it.id }
        expenses.filter { it.isPaid }
            .groupBy { YearMonth.from(it.date) }
            .map { (yearMonth, monthExpenses) ->
                val byCategory = monthExpenses.groupBy { it.categoryId }
                    .mapNotNull { (categoryId, exps) -> byId[categoryId]?.let { it to exps.sumOf { e -> e.amount } } }
                    .sortedByDescending { it.second }
                MonthStudy(yearMonth, monthExpenses.sumOf { it.amount }, byCategory)
            }
            .sortedByDescending { it.yearMonth }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPeriod(id: Long) {
        _selectedPeriodId.value = id
    }

    fun createPeriod(type: PeriodType, label: String, income: Double, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            val id = repository.createPeriod(type, label, income, startDate, endDate)
            _selectedPeriodId.value = id
        }
    }

    fun deletePeriod(period: Period) {
        viewModelScope.launch { repository.deletePeriod(period) }
    }

    /** Directly edits the period's base income (e.g. correcting the initial amount). */
    fun updatePeriodIncome(period: Period, newIncome: Double) {
        viewModelScope.launch { repository.updatePeriod(period.copy(income = newIncome)) }
    }

    /** Extra income received mid-period (a bonus, etc.) — adds on top of the base income, independent of expenses. */
    fun addIncome(description: String, amount: Double, date: LocalDate) {
        val periodId = _selectedPeriodId.value ?: return
        viewModelScope.launch { repository.addIncome(periodId, description, amount, date) }
    }

    fun deleteIncome(income: IncomeEntry) {
        viewModelScope.launch { repository.deleteIncome(income) }
    }

    fun addExpense(categoryId: Long, description: String, amount: Double, date: LocalDate) {
        val periodId = _selectedPeriodId.value ?: return
        viewModelScope.launch { repository.addExpense(periodId, categoryId, description, amount, date) }
    }

    fun togglePaid(expense: Expense) {
        viewModelScope.launch { repository.setExpensePaid(expense, !expense.isPaid) }
    }

    fun updateExpense(expense: Expense, categoryId: Long, description: String, amount: Double, date: LocalDate) {
        viewModelScope.launch {
            repository.updateExpense(expense.copy(categoryId = categoryId, description = description, amount = amount, date = date))
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun createCategory(name: String, iconKey: String, colorArgb: Long, onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.createCategory(name, iconKey, colorArgb)
            onCreated(id)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }
}

class GastosViewModelFactory(private val repository: GastosRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GastosViewModel(repository) as T
    }
}
