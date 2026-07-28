package com.android.gastos.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.gastos.data.CategoryEntity
import com.android.gastos.data.Expense
import com.android.gastos.ui.theme.color
import com.android.gastos.ui.theme.icon
import com.android.gastos.util.formatFullDate
import com.android.gastos.util.toLocalDateUtc
import com.android.gastos.util.toUtcMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddExpenseSheet(
    categories: List<CategoryEntity>,
    initialDate: LocalDate = LocalDate.now(),
    expenseToEdit: Expense? = null,
    onDismiss: () -> Unit,
    onAdd: (categoryId: Long, description: String, amount: Double, date: LocalDate) -> Unit,
    onUpdate: (expense: Expense, categoryId: Long, description: String, amount: Double, date: LocalDate) -> Unit = { _, _, _, _, _ -> },
    onCreateCategory: (name: String, iconKey: String, colorArgb: Long, onCreated: (Long) -> Unit) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf(expenseToEdit?.categoryId ?: categories.firstOrNull()?.id) }
    LaunchedEffect(categories) {
        if (selectedCategoryId == null || categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = categories.firstOrNull()?.id
        }
    }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(expenseToEdit?.description ?: "") }
    var amountText by remember { mutableStateOf(expenseToEdit?.amount?.let(::plainAmountString) ?: "") }
    var date by remember { mutableStateOf(expenseToEdit?.date ?: initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNewCategory by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    val selectedCategory = categories.find { it.id == selectedCategoryId }
    val isEditing = expenseToEdit != null

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(if (isEditing) "Editar gasto" else "Nuevo gasto", style = MaterialTheme.typography.headlineSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Selecciona una categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    leadingIcon = {
                        if (selectedCategory != null) {
                            Icon(selectedCategory.icon(), contentDescription = null, tint = selectedCategory.color())
                        }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        selectedCategoryId = option.id
                                        expanded = false
                                    },
                                    onLongClick = {
                                        if (!option.isDefault) {
                                            categoryPendingDelete = option
                                            expanded = false
                                        }
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Icon(option.icon(), contentDescription = null, tint = option.color())
                            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 12.dp))
                            Text(option.name, modifier = Modifier.weight(1f))
                            if (!option.isDefault) {
                                IconButton(
                                    onClick = {
                                        categoryPendingDelete = option
                                        expanded = false
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar categoría",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    DropdownMenuItem(
                        text = { Text("Nueva categoría") },
                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        onClick = {
                            expanded = false
                            showNewCategory = true
                        }
                    )
                }
            }

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ej. Súper, Uber, Renta...") },
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Monto") },
                placeholder = { Text("Ej. 350") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 8.dp))
                Text(formatFullDate(date), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Cambiar")
                }
            }

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 20.dp))

            val amount = amountText.toDoubleOrNull()
            Button(
                onClick = {
                    val categoryId = selectedCategoryId
                    if (categoryId != null && amount != null && amount > 0) {
                        if (expenseToEdit != null) {
                            onUpdate(expenseToEdit, categoryId, description, amount, date)
                        } else {
                            onAdd(categoryId, description, amount, date)
                        }
                        onDismiss()
                    }
                },
                enabled = selectedCategoryId != null && amount != null && amount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar cambios" else "Agregar gasto")
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 16.dp))
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = date.toUtcMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { date = it.toLocalDateUtc() }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showNewCategory) {
        NewCategorySheet(
            onDismiss = { showNewCategory = false },
            onCreate = { name, iconKey, colorArgb ->
                onCreateCategory(name, iconKey, colorArgb) { newId ->
                    selectedCategoryId = newId
                }
            }
        )
    }

    categoryPendingDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryPendingDelete = null },
            title = { Text("¿Eliminar \"${category.name}\"?") },
            text = { Text("Los gastos guardados con esta categoría también se eliminarán.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCategory(category)
                    categoryPendingDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryPendingDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun plainAmountString(amount: Double): String =
    if (amount == amount.toLong().toDouble()) amount.toLong().toString() else amount.toString()
