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
            repository.getTotalHydrationForDate(today).collect { total = it }
            val goal = repository.getOrCreateDailyGoal(today, repository.getBaseGoal(), false, null)
            val streak = repository.calculateStreak()
            Triple(total.toInt(), goal.adjustedGoalMl, streak)
        }
        
        val progress = ((currentMl.toFloat() / goalMl) * 100).toInt().coerceIn(0, 100)
        
        val views = RemoteViews(context.packageName, R.layout.widget_hydration)
        views.setTextViewText(R.id.widget_progress_text, "$currentMl / $goalMl ml")
        views.setTextViewText(R.id.widget_streak, "Streak: $streak")
        views.setProgressBar(R.id.widget_progress_bar, 100, progress, false)
        
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_title, openAppPendingIntent)
        
        val add250Intent = Intent(context, HydrationWidgetProvider::class.java).apply {
            action = HydrationWidgetProvider.ACTION_ADD_250
        }
        val add250PendingIntent = PendingIntent.getBroadcast(
            context, 1, add250Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_250, add250PendingIntent)
        
        val add500Intent = Intent(context, HydrationWidgetProvider::class.java).apply {
            action = HydrationWidgetProvider.ACTION_ADD_500
        }
        val add500PendingIntent = PendingIntent.getBroadcast(
            context, 2, add500Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_500, add500PendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
