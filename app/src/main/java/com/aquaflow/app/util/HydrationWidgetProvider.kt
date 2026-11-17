package com.aquaflow.app.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.aquaflow.app.MainActivity
import com.aquaflow.app.R
import com.aquaflow.app.domain.model.DrinkType
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HydrationWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_ADD_250 -> {
                addDrink(context, 250)
                updateAllWidgets(context)
            }
            ACTION_ADD_500 -> {
                addDrink(context, 500)
                updateAllWidgets(context)
            }
        }
    }
    
    private fun addDrink(context: Context, volumeMl: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val app = context.applicationContext as com.aquaflow.app.AquaFlowApp
            val repository = app.repository
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            repository.addDrink(DrinkType.WATER, volumeMl, today)
        }
    }
    
    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            android.content.ComponentName(context, HydrationWidgetProvider::class.java)
        )
        onUpdate(context, appWidgetManager, widgetIds)
    }
    
    companion object {
        const val ACTION_ADD_250 = "com.aquaflow.app.ACTION_ADD_250"
        const val ACTION_ADD_500 = "com.aquaflow.app.ACTION_ADD_500"
    }
}

private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        val app = context.applicationContext as com.aquaflow.app.AquaFlowApp
        val repository = app.repository
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        
        val (currentMl, goalMl, streak) = withContext(Dispatchers.IO) {
            var total = 0f
            repository.getTotalHydrationForDate(today).collect { total =
