package com.android.gastos.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromPeriodTypeName(name: String?): PeriodType? = name?.let { PeriodType.valueOf(it) }

    @TypeConverter
    fun toPeriodTypeName(type: PeriodType?): String? = type?.name
}
