package com.android.gastos.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.gastos.ui.GastosViewModel
import com.android.gastos.ui.MonthStudy
import com.android.gastos.ui.components.CategoryAmount
import com.android.gastos.ui.components.CategoryBarChart
import com.android.gastos.ui.components.DonutChart
import com.android.gastos.ui.theme.color
import com.android.gastos.ui.theme.icon
import com.android.gastos.util.formatMoney
import com.android.gastos.util.formatMonth

@Composable
fun StatsScreen(viewModel: GastosViewModel) {
    val categoryTotals by viewModel.categoryTotals.collectAsStateWithLifecycle()
    val monthlyStudy by viewModel.monthlyStudy.collectAsStateWithLifecycle()

    Crossfade(targetState = categoryTotals.isEmpty(), label = "stats-content", animationSpec = tween(300)) { isEmpty ->
        if (isEmpty) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    "Marca gastos como pagados para ver aquí tus estadísticas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val categoryItems = categoryTotals.map { (category, amount) ->
                CategoryAmount(category.name, category.icon(), amount, category.color())
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text("Gasto total por categoría", style = MaterialTheme.typography.titleLarge)
                }
                item {
                    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DonutChart(
                                values = categoryTotals.map { it.second },
                                colors = categoryItems.map { it.color }
                            )
                            Spacer(Modifier.height(20.dp))
                            CategoryBarChart(items = categoryItems, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                item {
                    Text("Estudio mensual", style = MaterialTheme.typography.titleLarge)
                }
                if (monthlyStudy.isEmpty()) {
                    item {
                        Text(
                            "Aún no hay historial mensual.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(monthlyStudy, key = { it.yearMonth }) { month ->
                        MonthCard(month, modifier = Modifier.animateItem(placementSpec = tween(300)))
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun MonthCard(month: MonthStudy, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatMonth(month.yearMonth),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(formatMoney(month.total), style = MaterialTheme.typography.titleMedium)
            }
            month.topCategory?.let { (category, amount) ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = category.color(),
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Mayor gasto: ${category.name} · ${formatMoney(amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            CategoryBarChart(
                items = month.byCategory.map { (category, amount) -> CategoryAmount(category.name, category.icon(), amount, category.color()) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
