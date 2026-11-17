package com.aquaflow.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquaflow.app.ui.components.GlassCard
import com.aquaflow.app.ui.theme.NeonCyan
import com.aquaflow.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Paramètres", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        
        SettingsSection(title = "Objectif quotidien") {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${uiState.dailyGoalMl} ml", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = uiState.dailyGoalMl.toFloat(),
                        onValueChange = { viewModel.updateDailyGoal(it.toInt()) },
                        valueRange = 1000f..4000f,
                        steps = 29
                    )
                    Text(text = "Glissez pour ajuster (1000 - 4000 ml)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
        
        SettingsSection(title = "Rappels intelligents") {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Activer les rappels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(text = "Toutes les ${uiState.reminderIntervalMinutes} minutes", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(checked = uiState.remindersEnabled, onCheckedChange = { viewModel.toggleReminders(it) })
                    }
                    
                    if (uiState.remindersEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Intervalle", style = MaterialTheme.typography.labelLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(30, 60, 90, 120).forEach { minutes ->
                                FilterChip(
                                    selected = uiState.reminderIntervalMinutes == minutes,
                                    onClick = { viewModel.updateReminderInterval(minutes) },
                                    label = { Text("${minutes}min") }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        SettingsSection(title = "Fonctionnalités avancées") {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Ajustement météo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(text = "+20% si température > 30°C", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(checked = uiState.weatherAdjustment, onCheckedChange = { viewModel.toggleWeatherAdjustment(it) })
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "AquaFlow v1.0.0", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun SettingsSection
