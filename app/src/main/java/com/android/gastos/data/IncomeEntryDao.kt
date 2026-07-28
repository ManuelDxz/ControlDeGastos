package com.android.gastos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeEntryDao {
    @Query("SELECT * FROM income_entries WHERE periodId = :periodId ORDER BY createdAt DESC")
    fun getIncomeForPeriod(periodId: Long): Flow<List<IncomeEntry>>

    @Query("SELECT * FROM income_entries")
    fun getAllIncome(): Flow<List<IncomeEntry>>

    @Insert
    suspend fun insertIncome(income: IncomeEntry): Long

    @Delete
    suspend fun deleteIncome(income: IncomeEntry)
}
