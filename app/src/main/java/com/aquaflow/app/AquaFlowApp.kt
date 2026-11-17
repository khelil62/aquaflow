package com.aquaflow.app

import android.app.Application
import androidx.room.Room
import com.aquaflow.app.data.local.AquaFlowDatabase
import com.aquaflow.app.data.repository.HydrationRepository
import com.aquaflow.app.util.WeatherManager

class AquaFlowApp : Application() {
    
    lateinit var database: AquaFlowDatabase
    lateinit var repository: HydrationRepository
    lateinit var weatherManager: WeatherManager
    
    override fun onCreate() {
        super.onCreate()
        
        database = Room.databaseBuilder(
            applicationContext,
            AquaFlowDatabase::class.java,
            "aquaflow_db"
        ).build()
        
        repository = HydrationRepository(
            database.drinkEntryDao(),
            database.dailyGoalDao()
        )
        
        weatherManager = WeatherManager(applicationContext)
    }
}
