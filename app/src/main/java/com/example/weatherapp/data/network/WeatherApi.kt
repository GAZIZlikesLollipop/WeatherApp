package com.example.weatherapp.data.network

import com.example.weatherapp.data.model.AirPollutionResponse
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.GeoTimeZoneResponse
import com.example.weatherapp.data.model.GeocodingResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("lang") lang : String,
        @Query("appid") appid : String,
        @Query("units") units : String
    ): CurrentWeatherResponse
    @GET("geo/1.0/direct")
    suspend fun getCoordinates(@Query("q") q : String, @Query("appid") appid: String): List<GeocodingResponse>
    @GET("/data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("appid") appid : String,
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("lang") lang : String,
        @Query("units") units : String
    ): WeatherForecastResponse
    @GET("https://api.geotimezone.com/public/timezone")
    suspend fun getTimeZone(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
    ): GeoTimeZoneResponse
    @GET("data/2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("appid") appid : String
    ): AirPollutionResponse
}
