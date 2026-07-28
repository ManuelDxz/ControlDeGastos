package com.android.gastos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.gastos.data.PeriodType
import com.android.gastos.util.periodBounds
import com.android.gastos.util.suggestedLabel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPeriodSheet(
    referenceDate: LocalDate = LocalDate.now(),
    initialType: PeriodType = PeriodType.QUINCENAL,
    onDismiss: () -> Unit,
    onCreate: (PeriodType, String, Double, LocalDate, LocalDate) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    val reference = remember { referenceDate }
    val bounds = remember(type) { periodBounds(type, reference) }
    val startDate = bounds.first
    val endDate = bounds.second
    var label by remember(type) { mutableStateOf(suggestedLabel(type, startDate, endDate)) }
    var incomeText by remember { mutableStateOf("") }
    var labelEdited by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Nuevo periodo", style = MaterialTheme.typography.headlineSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                PeriodType.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = PeriodType.entries.size),
                        onClick = {
                            type = option
                            if (!labelEdited) {
                                val (newStart, newEnd) = periodBounds(option, reference)
                                label = suggestedLabel(option, newStart, newEnd)
                            }
                        },
                        selected = type == option
                    ) {
                        Text(option.displayName)
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))

            OutlinedTextField(
                value = incomeText,
                onValueChange = { incomeText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Ingreso de este periodo") },
                placeholder = { Text("Ej. 4800") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))

            OutlinedTextField(
                value = label,
                onValueChange = {
                    label = it
                    labelEdited = true
                },
                label = { Text("Nombre del periodo") },
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            Text(
                "Del ${startDate} al ${endDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 20.dp))

            val income = incomeText.toDoubleOrNull()
            Button(
                onClick = {
                    if (income != null && income > 0 && label.isNotBlank()) {
                        onCreate(type, label, income, startDate, endDate)
                        onDismiss()
                    }
                },
                enabled = income != null && income > 0 && label.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear periodo")
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 16.dp))
        }
    }
}
