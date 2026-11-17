package com.aquaflow.app.data.repository

import com.aquaflow.app.data.local.dao.DrinkEntryDao
import com.aquaflow.app.data.local.dao.DailyGoalDao
import com.aquaflow.app.data.local.entity.DrinkEntry
import com.aquaflow.app.data.local.entity.DailyGoal
import com.aquaflow.app.domain.model.DrinkType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HydrationRepository(
    private val drinkEntryDao: DrinkEntryDao,
    private val dailyGoalDao: DailyGoalDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var baseGoalMl: Int = 2000
    
    fun getBaseGoal(): Int = baseGoalMl
    
    fun setBaseGoal(goal: Int) {
        baseGoalMl = goal
    }
    
    suspend fun addDrink(drinkType: DrinkType, volumeMl: Int, date: String) {
        val entry = DrinkEntry(
            drinkType = drinkType.name,
            volumeMl = volumeMl,
            hydrationFactor = drinkType.hydrationFactor,
            timestamp = System.currentTimeMillis(),
            date = date
        )
        drinkEntryDao.insert(entry)
    }
    
    fun getEntriesForDate(date: String): Flow<List<DrinkEntry>> {
        return drinkEntryDao.getEntriesForDate(date)
    }
    
    fun getTotalHydrationForDate(date: String): Flow<Float> {
        return drinkEntryDao.getTotalHydrationForDate(date)
    }
    
    suspend fun getHydrationForRange(startDate: String, endDate: String): Float {
        return drinkEntryDao.getTotalHydrationForRange(startDate, endDate)
    }
    
    suspend fun deleteEntriesForDate(date: String) {
        drinkEntryDao.deleteEntriesForDate(date)
    }
    
    suspend fun getOrCreateDailyGoal(date: String, baseGoal: Int, weatherAdjustment: Boolean, temp: Float?): DailyGoal {
        val existing = dailyGoalDao.getGoalForDate(date)
        if (existing != null) return existing
        
        val adjustedGoal = if (weatherAdjustment && (temp ?: 0f) > 30f) {
            (baseGoal * 1.2f).toInt()
        } else {
            baseGoal
        }
        
        val newGoal = DailyGoal(
            date = date,
            baseGoalMl = baseGoal,
            adjustedGoalMl = adjustedGoal,
            weatherAdjustment = weatherAdjustment,
            temperatureCelsius = temp
        )
        dailyGoalDao.insert(newGoal)
        return newGoal
    }
    
    suspend fun calculateStreak(): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        
        while (true) {
            val dateStr = currentDate.format(dateFormatter)
            val total = drinkEntryDao.getTotalHydrationForDate(dateStr).let { flow ->
                var result = 0f
                flow.collect { result = it }
                result
            }
            
            val goal = dailyGoalDao.getGoalForDate(dateStr)
            val goalValue = goal?.adjustedGoalMl ?: baseGoalMl
            
            if (total >= goalValue) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }
}
