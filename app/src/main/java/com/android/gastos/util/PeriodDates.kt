package com.android.gastos.util

import com.android.gastos.data.PeriodType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val labelDateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "MX"))
private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "MX"))

fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDateUtc(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun formatFullDate(date: LocalDate): String =
    date.format(fullDateFormatter).replaceFirstChar { it.uppercase() }

/**
 * Start/end bounds for a period, anchored to [reference]. Quincenas follow the
 * standard Mexican payroll split — days 1-15 and 16-30 — rather than a rolling
 * "+14 days" window, so picking Quincenal always lands on day 15 or day 30
 * (clamped to the 28/29 of February, since it has no day 30).
 */
fun periodBounds(type: PeriodType, reference: LocalDate): Pair<LocalDate, LocalDate> = when (type) {
    PeriodType.SEMANAL -> reference to reference.plusDays(6)
    PeriodType.QUINCENAL -> {
        if (reference.dayOfMonth <= 15) {
            reference.withDayOfMonth(1) to reference.withDayOfMonth(15)
        } else {
            val secondHalfEnd = minOf(30, reference.lengthOfMonth())
            reference.withDayOfMonth(16) to reference.withDayOfMonth(secondHalfEnd)
        }
    }
    PeriodType.MENSUAL -> reference.withDayOfMonth(1) to reference.withDayOfMonth(reference.lengthOfMonth())
}

fun suggestedLabel(type: PeriodType, start: LocalDate, end: LocalDate): String {
    val startText = start.format(labelDateFormatter)
    val endText = end.format(labelDateFormatter)
    return "${type.displayName} · $startText - $endText"
}
