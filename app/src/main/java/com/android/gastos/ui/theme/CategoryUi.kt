package com.android.gastos.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.gastos.data.CategoryEntity

fun CategoryEntity.icon(): ImageVector = iconFor(iconKey)

fun CategoryEntity.color(): Color = Color(colorArgb or 0xFF000000)
