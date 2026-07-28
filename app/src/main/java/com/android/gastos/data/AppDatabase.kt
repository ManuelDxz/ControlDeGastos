package com.android.gastos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Period::class, Expense::class, CategoryEntity::class, IncomeEntry::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun incomeEntryDao(): IncomeEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Seeding on onOpen (not onCreate) because destructive-migration recreates the tables
        // without re-invoking onCreate — onOpen runs on every open, so the empty-check keeps it idempotent.
        private val seedCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                val cursor = db.query("SELECT count(*) FROM categories")
                val isEmpty = cursor.use { it.moveToFirst() && it.getInt(0) == 0 }
                if (isEmpty) {
                    DefaultCategories.seeds.forEachIndexed { index, seed ->
                        db.execSQL(
                            "INSERT INTO categories (name, iconKey, colorArgb, isDefault, sortOrder) VALUES (?, ?, ?, 1, ?)",
                            arrayOf(seed.name, seed.iconKey, seed.colorArgb, index)
                        )
                    }
                }
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastos.db"
                )
                    .addCallback(seedCallback)
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
    }
}
