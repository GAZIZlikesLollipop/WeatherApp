package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.compose.WeatherAppTheme
import com.example.weatherapp.data.model.NetworkWeatherRepository
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.pressentation.main.WeatherViewModel
import com.example.weatherapp.pressentation.main.WeatherViewModelFactory
import com.example.weatherapp.utils.MyApp

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var weatherRepository: NetworkWeatherRepository  // Добавляем weatherRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userPreferencesRepository = (applicationContext as MyApp).userPreferencesRepository
        weatherRepository = (applicationContext as MyApp).weatherRepository
        val weatherViewModel : WeatherViewModel = ViewModelProvider(this, WeatherViewModelFactory(weatherRepository, userPreferencesRepository))[WeatherViewModel::class.java]

        setContent {
            WeatherAppTheme {
                AppNavigation()
            }
        }
    }
}
