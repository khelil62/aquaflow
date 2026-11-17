package com.aquaflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_entries")
data class DrinkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val drinkType: String,
    val volumeMl: Int,
    val hydrationFactor: Float,
    val timestamp: Long,
    val date: String
)
