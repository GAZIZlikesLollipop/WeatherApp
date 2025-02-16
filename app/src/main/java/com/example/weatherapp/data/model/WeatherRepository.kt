package com.example.weatherapp.data.model

import com.example.weatherapp.data.network.WeatherApi

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): CurrentWeatherResponse
    suspend fun geoCodeCoordinates(city: String): List<GeocodingResponse>
    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, units: String): WeatherForecastResponse
    suspend fun getTimeZone(latitude : Double, longitude : Double): String
    suspend fun getAirPollution(latitude : Double, longitude : Double): List<AirPollutionList>
}

class NetworkWeatherRepository(private val weatherApi: WeatherApi ): WeatherRepository{
    private val apiKey = "1984a519d90c1a7ea3f185dd67a2a818"
    override suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String, units: String): CurrentWeatherResponse {
        return weatherApi.getCurrentWeather(lat,lon,lang,apiKey,units)
    }

    override suspend fun geoCodeCoordinates(city: String): List<GeocodingResponse> {
        return weatherApi.getCoordinats(city, apiKey)
    }

    override suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String, units: String): WeatherForecastResponse {
        return  weatherApi.getWeatherForecast(apiKey,lat,lon,lang,units)
    }

    override suspend fun getTimeZone(latitude: Double, longitude: Double): String {
        return weatherApi.getTimeZone(latitude, longitude).iana_timezone
    }

    override suspend fun getAirPollution(latitude: Double, longitude: Double): List<AirPollutionList> {
        return weatherApi.getAirPollution(latitude,longitude, apiKey).list
    }
}