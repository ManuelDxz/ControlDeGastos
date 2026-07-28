package com.android.gastos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY startDate DESC, id DESC")
    fun getAllPeriods(): Flow<List<Period>>

    @Query("SELECT * FROM periods WHERE id = :periodId")
    fun getPeriodById(periodId: Long): Flow<Period?>

    @Insert
    suspend fun insertPeriod(period: Period): Long

    @Update
    suspend fun updatePeriod(period: Period)

    @Delete
    suspend fun deletePeriod(period: Period)
}
