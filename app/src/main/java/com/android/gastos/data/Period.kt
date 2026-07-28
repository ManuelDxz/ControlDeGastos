package com.android.gastos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "periods")
data class Period(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: PeriodType,
    val label: String,
    val income: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Long = System.currentTimeMillis()
)
