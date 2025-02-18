package com.example.weatherapp.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.data.model.NetworkWeatherRepository
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.data.network.RetrofitClient
import java.util.Calendar
import java.util.concurrent.TimeUnit


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
        scheduleWeatherAndNotification()
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

    private fun getDelayToTargetTime(hour: Int): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (currentTime.after(targetTime)) {
            currentTime.add(Calendar.DAY_OF_MONTH, 1)  // Если текущее время уже после targetTime, переносим на следующий день
        }

        return targetTime.timeInMillis - currentTime.timeInMillis
    }
    private fun scheduleWorkRequest(targetTimeHour: Int) {
        val delay = getDelayToTargetTime(targetTimeHour)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WeatherWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun scheduleWeatherAndNotification() {
        // Запуск задач для разных времен
        scheduleWorkRequest(19)
        scheduleWorkRequest(21)
        scheduleWorkRequest(11)
        scheduleWorkRequest(9)
    }
}
