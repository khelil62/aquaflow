package com.aquaflow.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.aquaflow.app.data.repository.HydrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(private val repository: HydrationRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        _uiState.update {
            it.copy(
                dailyGoalMl = repository.getBaseGoal(),
                remindersEnabled = true,
                reminderIntervalMinutes = 60,
                quietHoursStart = "22:00",
                quietHoursEnd = "07:00",
                weatherAdjustment = true,
                darkMode = true
            )
        }
    }
    
    fun updateDailyGoal(goal: Int) {
        repository.setBaseGoal(goal)
        _uiState.update { it.copy(dailyGoalMl = goal) }
    }
    
    fun toggleReminders(enabled: Boolean) {
        _uiState.update { it.copy(remindersEnabled = enabled) }
    }
    
    fun updateReminderInterval(minutes: Int) {
        _uiState.update { it.copy(reminderIntervalMinutes = minutes) }
    }
    
    fun toggleWeatherAdjustment(enabled: Boolean) {
        _uiState.update { it.copy(weatherAdjustment = enabled) }
    }
}

data class SettingsUiState(
    val dailyGoalMl: Int = 2000,
    val remindersEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 60,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val weatherAdjustment: Boolean = true,
    val darkMode: Boolean = true
)
