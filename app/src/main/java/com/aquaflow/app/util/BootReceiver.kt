package com.aquaflow.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("aquaflow_prefs", Context.MODE_PRIVATE)
            val remindersEnabled = prefs.getBoolean("reminders_enabled", true)
            val intervalMinutes = prefs.getInt("reminder_interval", 60)
            val quietStart = prefs.getString("quiet_start", "22:00") ?: "22:00"
            val quietEnd = prefs.getString("quiet_end", "07:00") ?: "07:00"
            
            if (remindersEnabled) {
                val scheduler = NotificationScheduler(context)
                scheduler.scheduleReminder(intervalMinutes, quietStart, quietEnd)
            }
        }
    }
}
