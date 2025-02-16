package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.compose.WeatherAppTheme
import com.example.weatherapp.data.model.NetworkWeatherRepository
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.data.network.RetrofitClient
import com.example.weatherapp.pressentation.main.WeatherViewModel
import com.example.weatherapp.pressentation.main.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "preferences")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userLocationRepository = UserPreferencesRepository(dataStore)
        val weatherRepository = NetworkWeatherRepository(RetrofitClient.weatherApi)
        val weatherViewModel : WeatherViewModel = ViewModelProvider(this, WeatherViewModelFactory(weatherRepository, userLocationRepository))[WeatherViewModel::class.java]

        setContent {
            WeatherAppTheme {
                AppNavigation()
            }
        }
    }
}
