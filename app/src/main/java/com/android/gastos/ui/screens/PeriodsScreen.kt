package com.android.gastos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.gastos.data.Expense
import com.android.gastos.data.IncomeEntry
import com.android.gastos.data.Period
import com.android.gastos.ui.GastosViewModel
import com.android.gastos.util.formatMoney
import androidx.compose.ui.unit.dp

@Composable
fun PeriodsScreen(viewModel: GastosViewModel, onNewPeriod: () -> Unit) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allIncome by viewModel.allIncome.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedPeriodId.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (periods.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    "No hay periodos todavía. Crea uno desde Inicio.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(periods, key = { it.id }) { period ->
                    val expensesForPeriod = allExpenses.filter { it.periodId == period.id }
                    val incomeForPeriod = allIncome.filter { it.periodId == period.id }
                    PeriodCard(
                        period = period,
                        expenses = expensesForPeriod,
                        extraIncome = incomeForPeriod,
                        isSelected = period.id == selectedId,
                        onSelect = { viewModel.selectPeriod(period.id) },
                        onDelete = { viewModel.deletePeriod(period) },
                        modifier = Modifier.animateItem(placementSpec = androidx.compose.animation.core.tween(300))
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        FloatingActionButton(
            onClick = onNewPeriod,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuevo periodo")
        }
    }
}

@Composable
private fun PeriodCard(
    period: Period,
    expenses: List<Expense>,
    extraIncome: List<IncomeEntry>,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paid = expenses.filter { it.isPaid }.sumOf { it.amount }
    val extra = extraIncome.sumOf { it.amount }
    val remaining = period.income + extra - paid
    val containerColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = androidx.compose.animation.core.tween(250),
        label = "period-card-color"
    )

    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        period.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${period.startDate} - ${period.endDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar periodo", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (extra > 0) "Ingreso: ${formatMoney(period.income + extra)}" else "Ingreso: ${formatMoney(period.income)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Pagado: ${formatMoney(paid)}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Resta: ${formatMoney(remaining)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
