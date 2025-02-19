package com.example.weatherapp.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.weatherapp.data.model.NetworkWeatherRepository
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.data.network.RetrofitClient
import java.util.Calendar


class MyApp : Application() {

    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var weatherRepository: NetworkWeatherRepository  // Добавляем weatherRepository

    override fun onCreate() {
        super.onCreate()
        // Инициализация репозиториев
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        weatherRepository = NetworkWeatherRepository(RetrofitClient.weatherApi)
        // Notifications
        createNotificationChannels()
        scheduleAlarm(this, 9, 3, 1001)
        scheduleAlarm(this, 11, 25, 1002)
        scheduleAlarm(this, 19, 5, 1003)
        scheduleAlarm(this, 21, 3, 1004)
    }

    private fun createNotificationChannel(channelId: String, channelName: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = description
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannels() {
        createNotificationChannel("tomorrow_forecast_channel", "Tomorrow forecast", "Tomorrow weather forecast notifications")
        createNotificationChannel("today_forecast_channel", "Today forecast", "Today weather forecast notifications")
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context, hour: Int, minute: Int, requestCode: Int) {
        // Получаем AlarmManager из системы
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Создаем Intent, который будет отправлен, когда сработает будильник
        val intent = Intent(context, AlarmReceiver::class.java)
        // Создаем PendingIntent, который будет запущен AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Создаем календарь для установки точного времени
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Если указанное время уже прошло сегодня, переносим на следующий день
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        Log.d("Alarm", "Будильник установлен на ${calendar.time}")
        // Используем setExactAndAllowWhileIdle для точного запуска задачи даже в Doze Mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

}
