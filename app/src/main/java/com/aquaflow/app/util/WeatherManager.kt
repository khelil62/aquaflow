package com.aquaflow.app.util

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WeatherManager(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private val apiKey = "VOTRE_CLE_API_ICI"
    private val baseUrl = "https://api.openweathermap.org/data/2.5/weather"
    
    suspend fun getCurrentWeather(): WeatherData? {
        return try {
            val location = getCurrentLocation() ?: return null
            val url = "$baseUrl?lat=${location.latitude}&lon=${location.longitude}&appid=$apiKey&units=metric&lang=fr"
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: return null)
                val temperature = JSONObject(json.get("main").toString()).getDouble("temp").toFloat()
                val description = json.getJSONArray("weather").let { weather ->
                    if (weather.length() > 0) JSONObject(weather.get(0).toString()).getString("description") else "clair"
                }
                val humidity = JSONObject(json.get("main").toString()).getInt("humidity")
                
                WeatherData(temperature = temperature, description = description, humidity = humidity)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    @Suppress("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}

data class WeatherData(val temperature: Float, val description: String, val humidity: Int)
