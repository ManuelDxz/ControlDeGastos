package com.android.gastos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.android.gastos.util.formatMoney
import androidx.compose.foundation.shape.RoundedCornerShape

data class CategoryAmount(val name: String, val icon: ImageVector, val amount: Double, val color: Color)

/**
 * Ranked horizontal bar list, one row per category: icon + label always beside
 * the color so identity never rests on hue alone, thin 14dp bar with rounded
 * ends, and a direct value label at the end of every bar (only a handful of
 * rows are ever shown at once, so per-row labels don't clutter).
 */
@Composable
fun CategoryBarChart(items: List<CategoryAmount>, modifier: Modifier = Modifier) {
    val maxAmount = items.maxOfOrNull { it.amount } ?: 0.0
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(item.name, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            formatMoney(item.amount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    val targetFraction = if (maxAmount > 0) (item.amount / maxAmount).toFloat().coerceIn(0.02f, 1f) else 0f
                    val animatedFraction by animateFloatAsState(
                        targetValue = targetFraction,
                        animationSpec = tween(durationMillis = 600),
                        label = "bar-fraction"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedFraction)
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(item.color)
                        )
                    }
                }
            }
        }
    }
}
