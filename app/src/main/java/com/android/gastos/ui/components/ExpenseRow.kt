package com.android.gastos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.android.gastos.data.CategoryEntity
import com.android.gastos.data.Expense
import com.android.gastos.ui.theme.color
import com.android.gastos.ui.theme.icon

@Composable
fun ExpenseRow(
    expense: Expense,
    category: CategoryEntity?,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = category?.name ?: "Sin categoría"
    val categoryColor = category?.color() ?: Color.Gray
    val categoryIcon = category?.icon() ?: Icons.Filled.MoreHoriz

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(checked = expense.isPaid, onCheckedChange = { onTogglePaid() })
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(categoryColor.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = categoryName,
                tint = categoryColor,
                modifier = Modifier.size(18.dp)
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.size(12.dp))
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onEdit)
        ) {
            Text(
                text = expense.description.ifBlank { categoryName },
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (expense.isPaid) TextDecoration.LineThrough else null,
                color = if (expense.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = com.android.gastos.util.formatMoney(expense.amount),
            style = MaterialTheme.typography.titleMedium,
            textDecoration = if (expense.isPaid) TextDecoration.LineThrough else null,
            color = if (expense.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }
    }
}
