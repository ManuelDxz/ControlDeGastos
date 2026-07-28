package com.android.gastos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.gastos.data.Expense
import com.android.gastos.data.Period
import com.android.gastos.ui.GastosViewModel
import com.android.gastos.ui.PeriodSummary
import com.android.gastos.ui.components.ExpenseRow
import com.android.gastos.ui.components.IncomeRow
import com.android.gastos.util.formatMoney

@Composable
fun HomeScreen(
    viewModel: GastosViewModel,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onNewPeriod: () -> Unit,
    onEditExpense: (Expense) -> Unit
) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedPeriodId.collectAsStateWithLifecycle()
    val summary by viewModel.periodSummary.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categoriesById = remember(categories) { categories.associateBy { it.id } }
    var showEditIncome by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PeriodSelector(
                periods = periods,
                selectedId = selectedId,
                onSelect = viewModel::selectPeriod,
                onNewPeriod = onNewPeriod,
                modifier = Modifier.padding(16.dp)
            )

            Crossfade(targetState = summary, label = "home-content") { current ->
                if (current == null) {
                    EmptyState(onNewPeriod = onNewPeriod)
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SummaryCard(
                            summary = current,
                            onEditIncome = { showEditIncome = true },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (current.extraIncome.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Ingresos extra",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(current.extraIncome, key = { "income-${it.id}" }) { income ->
                                    IncomeRow(
                                        income = income,
                                        onDelete = { viewModel.deleteIncome(income) },
                                        modifier = Modifier.animateItem(placementSpec = tween(300))
                                    )
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                }
                                item { Spacer(Modifier.height(8.dp)) }
                            }

                            item {
                                Text(
                                    text = "Gastos",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            if (current.expenses.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            "Aún no agregas gastos para este periodo.",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                items(current.expenses, key = { it.id }) { expense ->
                                    ExpenseRow(
                                        expense = expense,
                                        category = categoriesById[expense.categoryId],
                                        onTogglePaid = { viewModel.togglePaid(expense) },
                                        onDelete = { viewModel.deleteExpense(expense) },
                                        onEdit = { onEditExpense(expense) },
                                        modifier = Modifier.animateItem(placementSpec = tween(300))
                                    )
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                }
                            }
                            item { Spacer(Modifier.height(140.dp)) }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = summary != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(onClick = onAddIncome) {
                    Icon(Icons.Filled.TrendingUp, contentDescription = "Nuevo ingreso")
                }
                Spacer(Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    onClick = onAddExpense,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nuevo gasto") }
                )
            }
        }
    }

    if (showEditIncome && summary != null) {
        EditIncomeDialog(
            currentIncome = summary!!.period.income,
            onDismiss = { showEditIncome = false },
            onConfirm = { newIncome ->
                viewModel.updatePeriodIncome(summary!!.period, newIncome)
                showEditIncome = false
            }
        )
    }
}

@Composable
private fun EditIncomeDialog(
    currentIncome: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var text by remember { mutableStateOf(if (currentIncome == currentIncome.toLong().toDouble()) currentIncome.toLong().toString() else currentIncome.toString()) }
    val value = text.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar ingreso base") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Ingreso de este periodo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { value?.let { if (it > 0) onConfirm(it) } }, enabled = value != null && value > 0) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun PeriodSelector(
    periods: List<Period>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onNewPeriod: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = periods.find { it.id == selectedId }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f)) {
            Surface(
                onClick = { if (periods.isNotEmpty()) expanded = true },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Periodo actual",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = selected?.label ?: "Sin periodos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    if (periods.isNotEmpty()) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = { Text(period.label) },
                        onClick = {
                            onSelect(period.id)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(0.dp))
        TextButton(onClick = onNewPeriod) {
            Text("Nuevo")
        }
    }
}

@Composable
private fun SummaryCard(
    summary: PeriodSummary,
    onEditIncome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Saldo restante",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            val animatedRemaining by animateFloatAsState(
                targetValue = summary.remaining.toFloat(),
                animationSpec = tween(durationMillis = 500),
                label = "remaining-balance"
            )
            Text(
                text = formatMoney(animatedRemaining.toDouble()),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(12.dp))
            val totalIncome = summary.period.income + summary.totalExtraIncome
            val targetProgress = if (totalIncome > 0)
                (summary.totalPaid / totalIncome).toFloat().coerceIn(0f, 1f)
            else 0f
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 500),
                label = "paid-progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .wrapContentSize(unbounded = false),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SummaryStat("Ingreso", formatMoney(summary.period.income))
                    IconButton(onClick = onEditIncome, modifier = Modifier.size(22.dp)) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar ingreso base",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                SummaryStat("Pagado", formatMoney(summary.totalPaid))
                SummaryStat("Pendiente", formatMoney(summary.totalPending))
            }
            if (summary.totalExtraIncome > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "+ ${formatMoney(summary.totalExtraIncome)} de ingreso extra este periodo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun EmptyState(onNewPeriod: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Crea tu primer periodo (semana, quincena o mes) para empezar a llevar tus gastos.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            FloatingActionButton(onClick = onNewPeriod) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo periodo")
            }
        }
    }
}
