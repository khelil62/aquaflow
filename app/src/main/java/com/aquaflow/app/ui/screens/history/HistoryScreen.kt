package com.aquaflow.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquaflow.app.ui.components.GlassCard
import com.aquaflow.app.ui.theme.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "30 derniers jours", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Moyenne", value = "${uiState.averageMl}", unit = "ml/jour", modifier = Modifier.weight(1f))
                StatCard(title = "Objectifs", value = "${uiState.daysAchieved}/${uiState.totalDays}", unit = "jours", modifier = Modifier.weight(1f))
            }
        }
        
        items(uiState.dailyData) { day ->
            DailyCard(day)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = NeonCyan)
            Text(text = unit, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

@Composable
private fun DailyCard(day: DailyData) {
    val progress = (day.totalMl.toFloat() / day.goalMl).coerceIn(0f, 1f)
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = day.dayLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "${day.totalMl} / ${day.goalMl} ml", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(TextMuted.copy(alpha = 0.2f)))
                Box(
                    modifier = Modifier.size((56 * progress).dp).clip(CircleShape).background(
                        if (day.achieved) Brush.linearGradient(listOf(SuccessGreen, NeonCyan))
                        else Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    )
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (day.achieved) SuccessGreen else TextPrimary
                )
            }
        }
    }
}
