package com.aquaflow.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aquaflow.app.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        showNotification(context)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_reminders", "Rappels d'hydratation",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications pour vous rappeler de boire"
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(context: Context) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val messages = listOf(
            "💧 Il est temps de boire un verre d'eau !",
            "🌊 Votre corps a besoin d'hydratation",
            "💦 N'oubliez pas de boire !",
            "🥤 Un petit verre d'eau ?"
        )
        
        val notification = NotificationCompat.Builder(context, "hydration_reminders")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("AquaFlow")
            .setContentText(messages.random())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
