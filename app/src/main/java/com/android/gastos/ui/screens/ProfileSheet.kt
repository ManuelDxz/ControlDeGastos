package com.android.gastos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent

private const val PROFILE_PHOTO_URL =
    "https://lh3.googleusercontent.com/a/ACg8ocKicVMBPq9EEhjcw4eNIGsqfMTiSpi6gxPhWPKkB4AdHXFxuJs=s83-c-mo"

private data class LevelSkill(val name: String, val level: String)
private data class ToolSkill(val name: String, val icon: ImageVector)
private data class Badge(val label: String, val icon: ImageVector, val color: Color)

private val languageSkills = listOf(
    LevelSkill("Kotlin", "Avanzado"),
    LevelSkill("Java", "Avanzado"),
    LevelSkill("JavaScript", "Intermedio"),
    LevelSkill("TypeScript", "Intermedio"),
    LevelSkill("Python", "Intermedio"),
    LevelSkill("SQL", "Intermedio"),
    LevelSkill("C", "Básico"),
    LevelSkill("PHP", "Básico"),
    LevelSkill("Swift", "Básico"),
    LevelSkill("HTML / CSS", "Básico")
)

private val erpSkills = listOf(
    LevelSkill("COMPAQ", "Intermedio"),
    LevelSkill("PROSCAI", "Intermedio"),
    LevelSkill("SAPI", "Intermedio"),
    LevelSkill("Odoo", "Básico")
)

private val toolSkills = listOf(
    ToolSkill("Android Studio", Icons.Filled.Android),
    ToolSkill("VS Code", Icons.Filled.Code),
    ToolSkill("Git / GitHub", Icons.Filled.AccountTree),
    ToolSkill("Bases de datos (SQL)", Icons.Filled.Storage)
)

@Composable
private fun levelColor(level: String): Color = when (level) {
    "Avanzado" -> Color(0xFF0CA30C)
    "Intermedio" -> Color(0xFF2A78D6)
    else -> Color(0xFF757575)
}

private val badges = listOf(
    Badge("Desarrollador Android Studio", Icons.Filled.Android, Color(0xFF1BAF7A)),
    Badge("Desarrollador VS Code", Icons.Filled.Code, Color(0xFF2A78D6)),
    Badge("Colaborador Decimal Solution", Icons.Filled.Business, Color(0xFF6750A4))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    ProfilePhoto()
                    Spacer(Modifier.height(12.dp))
                    Text("@Disasterxz", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Manuel Morales",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            item {
                Text("Insignias", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(badges) { badge -> BadgeChip(badge) }
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                Text("Lenguajes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
            }
            items(languageSkills) { skill ->
                LevelRow(skill)
            }

            item {
                Spacer(Modifier.height(14.dp))
                Text("Sistemas ERP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
            }
            items(erpSkills) { skill ->
                LevelRow(skill)
            }

            item {
                Spacer(Modifier.height(14.dp))
                Text("Habilidades de informática", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    toolSkills.forEach { tool -> ToolRow(tool) }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfilePhoto() {
    SubcomposeAsyncImage(
        model = PROFILE_PHOTO_URL,
        contentDescription = "Foto de perfil de Manuel Morales",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        val state by painter.state.collectAsState()
        when (state) {
            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
            else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun BadgeChip(badge: Badge) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(badge.color.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .wrapContentWidth()
    ) {
        Icon(badge.icon, contentDescription = null, tint = badge.color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(badge.label, style = MaterialTheme.typography.labelLarge, color = badge.color)
    }
}

@Composable
private fun LevelRow(skill: LevelSkill) {
    val color = levelColor(skill.level)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(skill.name, style = MaterialTheme.typography.bodyLarge)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.16f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(skill.level, style = MaterialTheme.typography.labelLarge, color = color)
        }
    }
}

@Composable
private fun ToolRow(tool: ToolSkill) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(tool.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(tool.name, style = MaterialTheme.typography.bodyLarge)
    }
}
