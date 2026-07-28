package com.android.gastos.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.gastos.data.PeriodType
import com.android.gastos.ui.GastosViewModel
import com.android.gastos.ui.components.ExpenseRow
import com.android.gastos.util.formatFullDate
import com.android.gastos.util.formatMonth
import com.android.gastos.util.periodBounds
import com.android.gastos.util.suggestedLabel
import java.time.LocalDate
import java.time.YearMonth

private val weekdayLabels = listOf("L", "M", "M", "J", "V", "S", "D")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: GastosViewModel,
    onAddExpenseForDate: (LocalDate) -> Unit,
    onCreatePeriodForDate: (LocalDate, PeriodType) -> Unit,
    onEditExpense: (com.android.gastos.data.Expense) -> Unit
) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    var direction by remember { mutableStateOf(1) }
    var viewType by remember { mutableStateOf(PeriodType.QUINCENAL) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categoriesById = remember(categories) { categories.associateBy { it.id } }
    val expensesByDate = remember(allExpenses) { allExpenses.groupBy { it.date } }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { direction = -1; month = month.minusMonths(1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Mes anterior")
            }
            AnimatedContent(
                targetState = month,
                transitionSpec = {
                    val towards = if (direction >= 0) 1 else -1
                    (slideInHorizontally(tween(250)) { w -> towards * w } togetherWith
                        slideOutHorizontally(tween(250)) { w -> -towards * w })
                },
                label = "month-label"
            ) { shownMonth ->
                Text(
                    formatMonth(shownMonth),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = { direction = 1; month = month.plusMonths(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Mes siguiente")
            }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            PeriodType.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = PeriodType.entries.size),
                    onClick = { viewType = option },
                    selected = viewType == option
                ) {
                    Text(option.displayName)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            weekdayLabels.forEach { label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedContent(
            targetState = month,
            transitionSpec = {
                val towards = if (direction >= 0) 1 else -1
                (slideInHorizontally(tween(250)) { w -> towards * w } togetherWith
                    slideOutHorizontally(tween(250)) { w -> -towards * w })
            },
            label = "month-grid"
        ) { shownMonth ->
            val firstDay = shownMonth.atDay(1)
            val leadingBlanks = firstDay.dayOfWeek.value - 1 // Monday = 1 -> 0 blanks
            val daysInMonth = shownMonth.lengthOfMonth()
            val today = LocalDate.now()

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                items(leadingBlanks) { Box(modifier = Modifier.aspectRatio(1f)) }
                items(daysInMonth) { dayIndex ->
                    val date = shownMonth.atDay(dayIndex + 1)
                    val bucketIndex = when (viewType) {
                        PeriodType.SEMANAL -> (date.dayOfMonth - 1) / 7
                        PeriodType.QUINCENAL -> if (date.dayOfMonth <= 15) 0 else 1
                        PeriodType.MENSUAL -> 0
                    }
                    val bucketTint = if (bucketIndex % 2 == 0)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    val isToday = date == today
                    val hasExpenses = expensesByDate.containsKey(date)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bucketTint)
                            .then(
                                if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                else Modifier
                            )
                            .clickable { selectedDate = date },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${date.dayOfMonth}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            if (hasExpenses) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Toca un día para agregar un gasto o crear un periodo ahí.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    val tappedDate = selectedDate
    if (tappedDate != null) {
        val bounds = periodBounds(viewType, tappedDate)
        val alreadyExists = periods.any { it.type == viewType && it.startDate == bounds.first && it.endDate == bounds.second }
        val dayExpenses = expensesByDate[tappedDate].orEmpty()

        ModalBottomSheet(onDismissRequest = { selectedDate = null }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(formatFullDate(tappedDate), style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))

                if (dayExpenses.isNotEmpty()) {
                    Text("Gastos de este día", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    dayExpenses.forEach { expense ->
                        ExpenseRow(
                            expense = expense,
                            category = categoriesById[expense.categoryId],
                            onTogglePaid = { viewModel.togglePaid(expense) },
                            onDelete = { viewModel.deleteExpense(expense) },
                            onEdit = {
                                onEditExpense(expense)
                                selectedDate = null
                            }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                Button(
                    onClick = {
                        onAddExpenseForDate(tappedDate)
                        selectedDate = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar gasto en este día")
                }

                Spacer(Modifier.height(12.dp))

                val boundsLabel = suggestedLabel(viewType, bounds.first, bounds.second)
                OutlinedButton(
                    onClick = {
                        onCreatePeriodForDate(tappedDate, viewType)
                        selectedDate = null
                    },
                    enabled = !alreadyExists,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (alreadyExists) "Ya existe: $boundsLabel" else "Crear periodo: $boundsLabel")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
