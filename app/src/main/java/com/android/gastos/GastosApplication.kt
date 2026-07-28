package com.android.gastos

import android.app.Application
import com.android.gastos.data.AppDatabase
import com.android.gastos.data.GastosRepository

class GastosApplication : Application() {
    val repository: GastosRepository by lazy {
        GastosRepository(AppDatabase.getInstance(this))
    }
}
