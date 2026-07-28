package com.android.gastos.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** An extra income received mid-period (e.g. a bonus), on top of the period's base income. */
@Entity(
    tableName = "income_entries",
    foreignKeys = [
        ForeignKey(
            entity = Period::class,
            parentColumns = ["id"],
            childColumns = ["periodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("periodId")]
)
data class IncomeEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodId: Long,
    val description: String,
    val amount: Double,
    val date: LocalDate = LocalDate.now(),
    val createdAt: Long = System.currentTimeMillis()
)
