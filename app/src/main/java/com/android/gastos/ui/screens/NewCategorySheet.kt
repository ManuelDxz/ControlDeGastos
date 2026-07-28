package com.android.gastos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.gastos.ui.theme.CategoryColorSwatches
import com.android.gastos.ui.theme.IconCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCategorySheet(
    onDismiss: () -> Unit,
    onCreate: (name: String, iconKey: String, colorArgb: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIconKey by remember { mutableStateOf(IconCatalog.keys.first()) }
    var selectedColor by remember { mutableStateOf(CategoryColorSwatches.first()) }
    val selectedColorValue = Color(selectedColor or 0xFF000000)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Nueva categoría", style = MaterialTheme.typography.headlineSmall)
            VSpace(16.dp)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                placeholder = { Text("Ej. Mascota, Gimnasio...") },
                modifier = Modifier.fillMaxWidth()
            )

            VSpace(16.dp)
            Text("Color", style = MaterialTheme.typography.titleMedium)
            VSpace(8.dp)
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(96.dp)
            ) {
                items(CategoryColorSwatches) { swatch ->
                    val color = Color(swatch or 0xFF000000)
                    val isSelected = swatch == selectedColor
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = swatch },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            VSpace(16.dp)
            Text("Ícono", style = MaterialTheme.typography.titleMedium)
            VSpace(8.dp)
            LazyHorizontalGrid(
                rows = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                items(IconCatalog.entries.toList()) { entry ->
                    val key = entry.key
                    val icon = entry.value
                    val isSelected = key == selectedIconKey
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) selectedColorValue.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) selectedColorValue else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedIconKey = key },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = key,
                            tint = if (isSelected) selectedColorValue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            VSpace(24.dp)
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), selectedIconKey, selectedColor)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear categoría")
            }
            VSpace(16.dp)
        }
    }
}

@Composable
private fun VSpace(height: Dp) {
    androidx.compose.foundation.layout.Spacer(Modifier.height(height))
}
