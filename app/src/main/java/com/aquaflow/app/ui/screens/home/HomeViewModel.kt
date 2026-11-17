package com.aquaflow.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaflow.app.data.repository.HydrationRepository
import com.aquaflow.app.domain.model.DrinkType
import com.aquaflow.app.util.WeatherManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val repository: HydrationRepository,
    private val weatherManager: WeatherManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val today = LocalDate.now().format(dateFormatter)
    
    init {
        loadTodayData()
        observeHydration()
    }
    
    private fun loadTodayData() {
        viewModelScope.launch {
            val baseGoal = repository.getBaseGoal()
            val weather = weatherManager.getCurrentWeather()
            val adjustedGoal = if (weather != null && weather.temperature > 30f) {
                (baseGoal * 1.2f).toInt()
            } else {
                baseGoal
            }
            
            _uiState.update {
                it.copy(
                    dailyGoalMl = adjustedGoal,
                    weatherAdjustment = weather?.temperature?.let { temp -> temp > 30f } ?: false,
                    currentTemperature = weather?.temperature
                )
            }
        }
    }
    
    private fun observeHydration() {
        viewModelScope.launch {
            repository.getTotalHydrationForDate(today).collect { total ->
                _uiState.update {
                    it.copy(
                        currentHydrationMl = total.toInt(),
                        progress = total / it.dailyGoalMl
                    )
                }
            }
        }
    }
    
    fun addDrink(drinkType: DrinkType, volumeMl: Int) {
        viewModelScope.launch {
            repository.addDrink(drinkType, volumeMl, today)
        }
    }
    
    fun addWater(volumeMl: Int) = addDrink(DrinkType.WATER, volumeMl)
    fun addCoffee(volumeMl: Int) = addDrink(DrinkType.COFFEE, volumeMl)
    fun addTea(volumeMl: Int) = addDrink(DrinkType.TEA, volumeMl)
}

data class HomeUiState(
    val dailyGoalMl: Int = 2000,
    val currentHydrationMl: Int = 0,
    val progress: Float = 0f,
    val weatherAdjustment: Boolean = false,
    val currentTemperature: Float? = null,
    val streakDays: Int = 0
)
