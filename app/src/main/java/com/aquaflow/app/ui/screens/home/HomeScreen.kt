package com.aquaflow.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aquaflow.app.ui.components.GlassCard
import com.aquaflow.app.ui.components.QuickAddButton
import com.aquaflow.app.ui.components.WaterGlassIndicator
import com.aquaflow.app.ui.theme.NeonCyan
import com.aquaflow.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassCard(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Jour ${uiState.streakDays} 🔥",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonCyan,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            if (uiState.weatherAdjustment) {
                GlassCard(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "${uiState.currentTemperature?.toInt()}°C → +20%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth(0.8f).aspectRatio(0.7f)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                WaterGlassIndicator(
                    progress = uiState.progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxHeight(0.8f).fillMaxWidth(0.6f)
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.currentHydrationMl}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/ ${uiState.dailyGoalMl} ml",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            QuickAddButton(
                volume = 250,
                icon = { Icon(Icons.Default.LocalDrink, contentDescription = null, tint = NeonCyan) },
                onClick = { viewModel.addWater(250) }
            )
            
            QuickAddButton(
                volume = 500,
                icon = { Icon(Icons.Default.LocalDrink, contentDescription = null, tint = NeonCyan) },
                onClick = { viewModel.addWater(500) }
            )
            
            QuickAddButton(
                volume = 330,
                icon = { Icon(Icons.Default.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                onClick = { viewModel.addCoffee(330) }
            )
        }
    }
}
