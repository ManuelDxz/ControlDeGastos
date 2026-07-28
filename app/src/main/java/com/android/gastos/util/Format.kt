package com.android.gastos.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val mxLocale = Locale("es", "MX")

// Built manually (not NumberFormat.getCurrencyInstance) so the "$" + grouping/decimal
// style is fixed to Mexican pesos regardless of the device's own locale/ICU settings.
private val moneyFormat = DecimalFormat("$ #,##0.00", DecimalFormatSymbols(mxLocale))

fun formatMoney(amount: Double): String = moneyFormat.format(amount)

fun formatMonth(yearMonth: YearMonth): String {
    val name = yearMonth.month.getDisplayName(TextStyle.FULL, mxLocale)
        .replaceFirstChar { it.uppercase() }
    return "$name ${yearMonth.year}"
}
