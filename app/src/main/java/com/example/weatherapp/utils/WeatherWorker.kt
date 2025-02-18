package com.example.weatherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.data.model.NetworkWeatherRepository
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.data.model.Units
import com.example.weatherapp.data.network.RetrofitClient
import java.time.LocalTime

class WeatherWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val userPreferencesRepository = UserPreferencesRepository(context.dataStore)
    private val weatherRepository = NetworkWeatherRepository(RetrofitClient.weatherApi)
    private val name = context.resources.getStringArray(R.array.forecast_notifcation)
    private val day_channel = "tomorrow_forecast_channel"
    private val night_channel = "today_forecast_channel"
    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        Log.d("WeatherWorker", "Work started!")
        try {
            val (city, language) = userPreferencesRepository.getUserPreferences()
            val units = userPreferencesRepository.getWeatherUnits()
            val temp: String = when (units) {
                Units().metric -> "C°"
                Units().imperial -> "F°"
                else -> "K"
            }

            val geocodingResponse = weatherRepository.geoCodeCoordinates(city)
            val latitude = geocodingResponse.firstOrNull()?.lat ?: 0.0
            val longitude = geocodingResponse.firstOrNull()?.lon ?: 0.0

            val currentTime = LocalTime.now() // Получаем текущее время
            val evening19 = LocalTime.of(19, 0)
            val evening21 = LocalTime.of(21, 0)

            val outPut : Quadruple<Int, Int, Int, String> = when {
                currentTime.isAfter(evening21) -> Quadruple(4, 1, 0, night_channel)
                currentTime.isAfter(evening19) -> Quadruple(5, 1, 0, night_channel)
                else -> Quadruple(1, 3, 2, day_channel)
            }

            val weatherResponse = weatherRepository.getWeatherForecast(latitude, longitude, language, units)
            val temperature = weatherResponse.list[outPut.first].main.temp
            val description = weatherResponse.list[outPut.first].weather.firstOrNull()?.description

            val forecastText = "${name[outPut.second]} $description $temperature$temp"
            NotificationHelper.showNotification(applicationContext, name[outPut.third], forecastText, 1, outPut.fourth)

            return Result.success()
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Ошибка при получении данных: ${e.message}")
            return Result.retry() // Повторить попытку в случае ошибки
        }
    }
}
