package com.aquaflow.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaflow.app.data.repository.HydrationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryViewModel(private val repository: HydrationRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    init {
        loadLast30Days()
    }
    
    private fun loadLast30Days() {
        viewModelScope.launch {
            val endDate = LocalDate.now()
            val dailyData = mutableListOf<DailyData>()
            
            for (i in 0..29) {
                val date = endDate.minusDays(i.toLong())
                val dateStr = date.format(dateFormatter)
                val total = repository.getHydrationForRange(dateStr, dateStr)
                val goal = repository.getOrCreateDailyGoal(dateStr, repository.getBaseGoal(), false, null)
                
                dailyData.add(
                    DailyData(
                        date = dateStr,
                        dayLabel = date.format(DateTimeFormatter.ofPattern("EEE dd")),
                        totalMl = total.toInt(),
                        goalMl = goal.adjustedGoalMl,
                        achieved = total >= goal.adjustedGoalMl
                    )
                )
            }
            
            val avgMl = if (dailyData.isNotEmpty()) dailyData.map { it.totalMl }.average().toInt() else 0
            val daysAchieved = dailyData.count { it.achieved }
            
            _uiState.update {
                it.copy(dailyData = dailyData, averageMl = avgMl, daysAchieved = daysAchieved, totalDays = dailyData.size)
            }
        }
    }
}

data class HistoryUiState(
    val dailyData: List<DailyData> = emptyList(),
    val averageMl: Int = 0,
    val daysAchieved: Int = 0,
    val totalDays: Int = 0
)

data class DailyData(
    val date: String,
    val dayLabel: String,
    val totalMl: Int,
    val goalMl: Int,
    val achieved: Boolean
)
