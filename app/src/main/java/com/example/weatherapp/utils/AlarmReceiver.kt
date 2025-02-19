package com.example.weatherapp.utils
// AlarmReceiver.kt
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm triggered!")
        // Проверяем, что context не равен null
        context?.let {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            // Создаем однократный WorkManager запрос для WeatherWorker
            val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setConstraints(constraints)
                .build()
            // Добавляем задачу в очередь WorkManager
            WorkManager.getInstance(it).enqueue(workRequest)
        }
    }
}
