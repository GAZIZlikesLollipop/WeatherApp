package com.example.weatherapp.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R

object NotificationHelper {

    fun showNotification(context: Context, title: String, message: String, notifi_id : Int, channel_id : String) {
        // Используем applicationContext, чтобы избежать утечек Activity
        val appContext = context.applicationContext
        val icon: Int = if (channel_id.equals("today_forecast_channel", ignoreCase = true)) R.drawable.rounded_clear_day_24 else R.drawable.outline_dark_mode_24
// Создаём Intent для открытия MainActivity при нажатии на уведомление
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Строим уведомление
        val notification = NotificationCompat.Builder(appContext, channel_id)
            .setSmallIcon(icon) // Замените на свой ресурс иконки
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Получаем NotificationManager и показываем уведомление
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notifi_id, notification)
    }
}
