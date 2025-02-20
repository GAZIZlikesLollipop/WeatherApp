package com.example.weatherapp.pressentation.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.AirPollutionList
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastCity
import com.example.weatherapp.data.model.ForecastList
import com.example.weatherapp.data.model.Units
import com.example.weatherapp.data.model.UserPreferencesRepository
import com.example.weatherapp.data.model.WeatherRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface WeatherUiState {
    data class Success(val info: CurrentWeatherResponse): WeatherUiState
    data object Error : WeatherUiState
    data object Loading : WeatherUiState
}

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    var weatherUiState : WeatherUiState by mutableStateOf(WeatherUiState.Loading)
        private set
    var language : String by mutableStateOf("en")
    var city : String by mutableStateOf("")
    var units : String by mutableStateOf(Units().standard)

    private val _forecastList = MutableLiveData<List<ForecastList>>()
    val forecastList: LiveData<List<ForecastList>?> = _forecastList

    private val _forecastCity = MutableStateFlow<ForecastCity?>(null)
    val forecastCity: StateFlow<ForecastCity?> = _forecastCity.asStateFlow()

    private val _airPollution = MutableStateFlow<List<AirPollutionList>?>(null)
    val airPollution: StateFlow<List<AirPollutionList>?> = _airPollution.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    var timeZone : String by mutableStateOf("UTF")

    init{
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.getUserPreferences()
            city = userPreferences.first
            language = userPreferences.second
            Log.d("UserPreferences", "Current language is $language")
            units = userPreferencesRepository.getWeatherUnits()
            fetchCurrentWeather()
            fetchWeatherForecast()
            fetchAirPollution()
            fetchTimeZone()
        }
    }

    fun fetchCurrentWeather(){
        viewModelScope.launch {
            val (lat, lon) = fetchCoordinates().await()
            weatherUiState = try {
                val response = weatherRepository.getCurrentWeather(
                    lat = lat,
                    lon = lon,
                    lang = language,
                    units = units
                )

                WeatherUiState.Success(
                    response.copy(
                        wind = response.wind.copy(
                            speed = convertWindSpeed(response.wind.speed),
                            gust = response.wind.gust?.let { convertWindSpeed(it) }
                        )
                    )
                )

            }catch (e: IOException) {
                Log.e("BookViewModel", "Network Error: ${e.message}")
                WeatherUiState.Error
            }catch (e: HttpException) {
                Log.e("BookViewModel", "HTTP Error: ${e.message}")
                WeatherUiState.Error
            }
        }
    }

    private fun convertWindSpeed(speed: Double): Double {
        return when (units) {
            Units().standard -> speed * 3.6 // м/с → км/ч
            Units().imperial -> speed * 1609.344 * 1 / 1000 * 3600 // м/с → мили/ч
            else -> speed
        }
    }

    fun saveUnit(unit : String){
        viewModelScope.launch {
            units = unit
            userPreferencesRepository.saveWeatherUnits(unit)
        }
    }

    fun saveCity(cite : String){
        viewModelScope.launch {
            city = cite
            userPreferencesRepository.saveUserPreferences(cite, language)
        }
    }
    fun saveLanguage(lang : String){
        viewModelScope.launch {
            language = lang
            userPreferencesRepository.saveUserPreferences(city, lang)
        }
    }

    fun fetchWeatherForecast() {
        viewModelScope.launch {
            val (lat, lon) = fetchCoordinates().await()
            try{
                val response = weatherRepository.getWeatherForecast(lat = lat, lon = lon, lang = language, units = units)
                _forecastList.value = response.list
                _forecastCity.value = response.city
            }catch (e: IOException) {
                Log.e("ForecastApi", "Network Error: ${e.message}")
                _forecastList.value = emptyList()
                _forecastCity.value = null
            }catch (e: HttpException) {
                Log.e("ForecastApi", "HTTP Error: ${e.message}")
                _forecastList.value = emptyList()
                _forecastCity.value = null
            }
        }
    }

    fun fetchTimeZone(){
        viewModelScope.launch {
            try {
                val response = fetchCoordinates().await()
                timeZone = weatherRepository.getTimeZone(response.first, response.second)
            }catch(e : Exception){
                Log.e("TimeZoneAPiError", "Error")
                timeZone = null.toString()
            }
        }
    }
    fun fetchAirPollution(){
        viewModelScope.launch {
            try{
                val coordinates = fetchCoordinates().await()
                _airPollution.value = weatherRepository.getAirPollution(coordinates.first, coordinates.second)
            }catch (e: IOException) {
                Log.e("AirPollution", "Network Error: ${e.message}")
                _airPollution.value = emptyList()
            }catch (e: HttpException) {
                Log.e("AirPollution", "HTTP Error: ${e.message}")
                _airPollution.value = emptyList()
            }
        }
    }
    private fun fetchCoordinates(): Deferred<Pair<Double, Double>> {
        return viewModelScope.async {
            try {
                val cityName = userPreferencesRepository.getUserPreferences().first
                val response = weatherRepository.geoCodeCoordinates(cityName).firstOrNull()
                Pair(response?.lat ?: 51.5073219, response?.lon ?: -0.1276474)
            } catch (e: HttpException) {
                Log.e("Coordinates", "HTTP Error: ${e.code()} - ${e.message()}")
                Pair(51.5073219, -0.1276474) // London (fallback)
            } catch (e: IOException) {
                Log.e("Coordinates", "Network Error: ${e.message}")
                Pair(51.5073219, -0.1276474)
            }
        }
    }
}

class WeatherViewModelFactory(private val weatherRepository: WeatherRepository, private val userPreferencesRepository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
