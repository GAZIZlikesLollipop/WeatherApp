package com.example.weatherapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherResponse(
    val weather : List<CurrentWeather>,
    val main : CurrentMain,
    val visibility : Int?,
    val wind : CurrentWind,
    val clouds: CurrentClouds,
    val rain: CurrentRain? = null,
    val snow: CurrentSnow? = null,
    val timezone: Int
)
@Serializable
data class CurrentWeather(
    val main: String,
    val description: String,
    val icon: String
)
@Serializable
data class CurrentMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure : Int,
    val grnd_level: Int,
    val humidity: Int,
    val dt : Long? = null,
    val visibility: Int? = null
)
@Serializable
data class CurrentWind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)
@Serializable
data class CurrentClouds(
    val all: Int
)

@Serializable
data class CurrentRain(
    @SerialName("1h") val speed: Double
)

@Serializable
data class CurrentSnow(
    @SerialName("1h") val speed: Double
)

@Serializable
data class GeocodingResponse(
    val lat : Double,
    val lon : Double,
    val name: String,
    val country : String
)

@Serializable
data class WeatherForecastResponse(
    val list: List<ForecastList>,
    val city: ForecastCity
)
@Serializable
data class ForecastList(
    val dt : Long,
    val weather : List<CurrentWeather>,
    val main : CurrentMain,
    val pop : Double,
    val rain: ForecastRain? = null,
    val snow: ForecastSnow? = null,
    val sys: Sys
)
@Serializable
data class Sys(
    val pod : String
)
@Serializable
data class ForecastCity(
    val sunrise: Long,
    val sunset: Long
)

@Serializable
data class ForecastRain(
    @SerialName("3h")val volume: Double
)
@Serializable
data class ForecastSnow(
    @SerialName("3h")val volume: Double
)
@Serializable
data class GeoTimeZoneResponse(
    val iana_timezone  : String
)
@Serializable
data class AirPollutionResponse(
    val list : List<AirPollutionList>
)
@Serializable
data class AirPollutionList(
    val dt : Long,
    val main: APMain,
    val components : AirComponents
)
@Serializable
data class AirComponents(
    val pm2_5 : Double,
    val pm10 : Double,
    val co : Double,
    val o3 : Double,
    val no2 : Double,
    val so2 : Double,
    val nh3: Double
)
@Serializable
data class APMain(
    val aqi : Int
)
data class Units(
    val standard : String = "standard",
    val metric : String = "metric",
    val imperial : String = "imperial"
)
