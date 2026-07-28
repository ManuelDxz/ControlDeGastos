package com.android.gastos.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GastosRepository(private val db: AppDatabase) {

    fun getAllPeriods(): Flow<List<Period>> = db.periodDao().getAllPeriods()

    fun getExpensesForPeriod(periodId: Long): Flow<List<Expense>> =
        db.expenseDao().getExpensesForPeriod(periodId)

    fun getAllExpenses(): Flow<List<Expense>> = db.expenseDao().getAllExpenses()

    fun getAllCategories(): Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()

    suspend fun createCategory(name: String, iconKey: String, colorArgb: Long): Long =
        db.categoryDao().insertCategory(CategoryEntity(name = name, iconKey = iconKey, colorArgb = colorArgb))

    suspend fun deleteCategory(category: CategoryEntity) = db.categoryDao().deleteCategory(category)

    suspend fun createPeriod(
        type: PeriodType,
        label: String,
        income: Double,
        startDate: LocalDate,
        endDate: LocalDate
    ): Long = db.periodDao().insertPeriod(
        Period(type = type, label = label, income = income, startDate = startDate, endDate = endDate)
    )

    suspend fun deletePeriod(period: Period) = db.periodDao().deletePeriod(period)

    suspend fun updatePeriod(period: Period) = db.periodDao().updatePeriod(period)

    fun getIncomeForPeriod(periodId: Long): Flow<List<IncomeEntry>> =
        db.incomeEntryDao().getIncomeForPeriod(periodId)

    fun getAllIncome(): Flow<List<IncomeEntry>> = db.incomeEntryDao().getAllIncome()

    suspend fun addIncome(periodId: Long, description: String, amount: Double, date: LocalDate): Long =
        db.incomeEntryDao().insertIncome(IncomeEntry(periodId = periodId, description = description, amount = amount, date = date))

    suspend fun deleteIncome(income: IncomeEntry) = db.incomeEntryDao().deleteIncome(income)

    suspend fun addExpense(
        periodId: Long,
        categoryId: Long,
        description: String,
        amount: Double,
        date: LocalDate
    ): Long = db.expenseDao().insertExpense(
        Expense(periodId = periodId, categoryId = categoryId, description = description, amount = amount, date = date)
    )

    suspend fun setExpensePaid(expense: Expense, isPaid: Boolean) =
        db.expenseDao().updateExpense(expense.copy(isPaid = isPaid))

    suspend fun updateExpense(expense: Expense) = db.expenseDao().updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = db.expenseDao().deleteExpense(expense)
}
