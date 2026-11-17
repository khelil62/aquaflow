package com.aquaflow.app.data.local.dao

import androidx.room.*
import com.aquaflow.app.data.local.entity.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: DailyGoal)
    
    @Query("SELECT * FROM daily_goals WHERE date = :date LIMIT 1")
    suspend fun getGoalForDate(date: String): DailyGoal?
    
    @Query("SELECT * FROM daily_goals ORDER BY date DESC LIMIT 30")
    fun getRecentGoals(): Flow<List<DailyGoal>>
    
    @Query("DELETE FROM daily_goals WHERE date = :date")
    suspend fun deleteGoalForDate(date: String)
}
