package com.android.gastos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private const val DECIMAL_SOLUTION_URL = "https://decimalsolution.com/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsSheet(onDismiss: () -> Unit, onOpenProfile: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Savings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Control de Gastos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "App creada por @Disasterxz",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Manuel Morales · Decimal Solution",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.padding(top = 24.dp).fillMaxWidth()) {
                OutlinedButton(
                    onClick = { uriHandler.openUri(DECIMAL_SOLUTION_URL) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Language, contentDescription = null)
                    Row(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Decimal Solution")
                    }
                }
                OutlinedButton(
                    onClick = onOpenProfile,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null)
                    Row(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Perfil de Manuel Morales")
                    }
                }
            }
        }
    }
}
