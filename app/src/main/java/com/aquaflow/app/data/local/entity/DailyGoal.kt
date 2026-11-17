package com.aquaflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey val date: String,
    val baseGoalMl: Int,
    val adjustedGoalMl: Int,
    val weatherAdjustment: Boolean,
    val temperatureCelsius: Float?
)
