package com.aquaflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aquaflow.app.data.local.dao.DrinkEntryDao
import com.aquaflow.app.data.local.dao.DailyGoalDao
import com.aquaflow.app.data.local.entity.DrinkEntry
import com.aquaflow.app.data.local.entity.DailyGoal

@Database(
    entities = [DrinkEntry::class, DailyGoal::class],
    version = 1,
    exportSchema = false
)
abstract class AquaFlowDatabase : RoomDatabase() {
    abstract fun drinkEntryDao(): DrinkEntryDao
    abstract fun dailyGoalDao(): DailyGoalDao
}
