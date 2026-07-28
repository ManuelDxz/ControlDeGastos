package com.android.gastos.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.gastos.util.formatMoney

/**
 * A ranked donut chart. Segments keep a 2dp surface gap between them (per the
 * data-viz mark spec) so adjacent slices never visually fuse, and the total is
 * shown as the center label since color alone never carries the amount. Sweeps
 * draw in from zero (Material-motion "emphasized" easing) whenever the data set
 * changes, rather than popping in at full size.
 */
@Composable
fun DonutChart(
    values: List<Double>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidthDp: Float = 28f
) {
    val total = values.sum()
    val sweepProgress = remember { Animatable(0f) }
    LaunchedEffect(values) {
        sweepProgress.snapTo(0f)
        sweepProgress.animateTo(1f, animationSpec = tween(durationMillis = 700))
    }
    val animatedTotal by animateFloatAsState(
        targetValue = total.toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "donut-total"
    )

    Box(modifier = modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokeWidth = strokeWidthDp.dp.toPx()
            val gapDegrees = if (total > 0) 2.5f else 0f
            val diameter = size.minDimension - strokeWidth
            val topLeft = androidx.compose.ui.geometry.Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)
            var startAngle = -90f
            if (total <= 0.0) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Butt)
                )
            } else {
                values.forEachIndexed { index, value ->
                    val fullSweep = (value / total * 360.0).toFloat()
                    val sweep = (fullSweep - gapDegrees) * sweepProgress.value
                    if (sweep > 0f) {
                        drawArc(
                            color = colors.getOrElse(index) { Color.Gray },
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                    startAngle += fullSweep
                }
            }
        }
        Text(
            text = formatMoney(animatedTotal.toDouble()),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
