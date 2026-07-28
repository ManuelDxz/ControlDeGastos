package com.android.gastos.data

/** Seed rows inserted once when the database is first created (see AppDatabase's Callback). */
object DefaultCategories {
    data class Seed(val name: String, val iconKey: String, val colorArgb: Long)

    val seeds: List<Seed> = listOf(
        Seed("Comida", "restaurant", 0xFF2A78D6),
        Seed("Gasto de la Semana", "calendar_view_week", 0xFF8D6E63),
        Seed("Quincena", "date_range", 0xFF3949AB),
        Seed("Gastos Médicos", "local_hospital", 0xFF1BAF7A),
        Seed("Compras en Línea", "shopping_cart", 0xFFD55181),
        Seed("Préstamos", "credit_card", 0xFF6750A4),
        Seed("Transporte", "directions_bus", 0xFFEB6834),
        Seed("Agua", "water_drop", 0xFF2E7D32),
        Seed("Gas", "local_gas_station", 0xFFD32F2F),
        Seed("Internet", "wifi", 0xFFC98500),
        Seed("Otro", "more_horiz", 0xFF757575)
    )
}
