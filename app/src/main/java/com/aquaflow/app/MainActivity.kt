package com.aquaflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aquaflow.app.ui.screens.home.HomeScreen
import com.aquaflow.app.ui.screens.history.HistoryScreen
import com.aquaflow.app.ui.screens.settings.SettingsScreen
import com.aquaflow.app.ui.theme.AquaFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AquaFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AquaFlowNavigation()
                }
            }
        }
    }
}

@Composable
fun AquaFlowNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("history") { HistoryScreen() }
        composable("settings") { SettingsScreen() }
    }
}
