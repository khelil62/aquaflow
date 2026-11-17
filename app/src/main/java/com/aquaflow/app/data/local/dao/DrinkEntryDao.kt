package com.aquaflow.app.data.local.dao

import androidx.room.*
import com.aquaflow.app.data.local.entity.DrinkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkEntryDao {
    
    @Insert
    suspend fun insert(entry: DrinkEntry)
    
    @Query("SELECT * FROM drink_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesForDate(date: String): Flow<List<DrinkEntry>>
    
    @Query("""
        SELECT COALESCE(SUM(volumeMl * hydrationFactor), 0) 
        FROM drink_entries 
        WHERE date = :date
    """)
    fun getTotalHydrationForDate(date: String): Flow<Float>
    
    @Query("""
        SELECT COALESCE(SUM(volumeMl * hydrationFactor), 0) 
        FROM drink_entries 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalHydrationForRange(startDate: String, endDate: String): Float
    
    @Query("DELETE FROM drink_entries WHERE date = :date")
    suspend fun deleteEntriesForDate(date: String)
}
