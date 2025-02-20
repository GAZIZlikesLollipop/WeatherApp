package com.example.weatherapp.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private val json = Json {
        ignoreUnknownKeys = true // Игнорирует неизвестные поля в JSON
        isLenient = true // Разрешает гибкий синтаксис JSON
    }

    private const val BASE_URL = "https://api.openweathermap.org/"

    @OptIn(ExperimentalSerializationApi::class)
    private val weather_retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Указание базового URL
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())) // Конвертер для JSON
            .build()
    }

    val weatherApi: WeatherApi by lazy{
        weather_retrofit.create(WeatherApi::class.java)
    }
}