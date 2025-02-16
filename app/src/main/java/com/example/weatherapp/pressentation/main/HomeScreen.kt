package com.example.weatherapp.pressentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.weatherapp.pressentation.components.ErrorScreen
import com.example.weatherapp.pressentation.components.HomeTopBar
import com.example.weatherapp.pressentation.components.Loading
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.compose.AQI_Good
import com.example.compose.AQI_Moderate
import com.example.compose.AQI_Neutral
import com.example.compose.AQI_Unhealthy
import com.example.compose.AQI_UnhealthySensitive
import com.example.compose.AQI_VeryUnhealthy
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AirPollutionList
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastCity
import com.example.weatherapp.data.model.ForecastList
import com.example.weatherapp.data.model.Units
import com.example.weatherapp.pressentation.components.FiveDayForecastCard
import com.example.weatherapp.pressentation.components.TwoDayForecastCard
import com.example.weatherapp.pressentation.components.MDCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WeatherViewModel
){
    val uiState = viewModel.weatherUiState
    val isLoading by viewModel.isLoading.collectAsState()
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1377CB), Color(0xFF00C1FF)) // От синего к голубому
    )
    val forecastList by viewModel.forecastList.observeAsState()
    val forecastCity by viewModel.forecastCity.collectAsState()
    val aqi by viewModel.airPollution.collectAsState()

    Scaffold(
        topBar = { HomeTopBar(navController,viewModel)},
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(gradient)) {
                when (uiState) {
                    is WeatherUiState.Success -> {
                        LaunchedEffect(viewModel.units == Units().metric) {
                            when(viewModel.units){
                                Units().standard -> {
                                    if(uiState.info.wind.gust != null) uiState.info.wind.gust * 3.6
                                    uiState.info.wind.speed * 3.6
                                }
                                Units().imperial ->{
                                    if(uiState.info.wind.gust != null)  uiState.info.wind.gust * 1609.344 * 1 / 1000 * 3600
                                    uiState.info.wind.speed * 1609.344 * 1/1000 * 3600
                                }
                            }
                        }
                        aqi?.let { it1 ->
                            WeatherData(uiState.info,
                                { viewModel.fetchCurrentWeather()
                                    viewModel.fetchWeatherForecast()
                                    viewModel.fetchAirPollution()
                                    viewModel.fetchTimeZone()
                                }, isLoading, forecastList,forecastCity, viewModel.language, viewModel.timeZone, it1, viewModel.units
                            )
                        }
                    }
                    is WeatherUiState.Error -> { ErrorScreen(viewModel) }

                    is WeatherUiState.Loading -> { Loading() }
                }
            }
        }
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherData(
    data: CurrentWeatherResponse,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    forecastList: List<ForecastList>?,
    forecastCity: ForecastCity?,
    language: String,
    timeZone: String,
    aqi : List<AirPollutionList>,
    units : String
){
    val arr = stringArrayResource(R.array.rain_snow)
    val temperature = when(units){
        Units().metric -> "C°"
        Units().imperial -> "F°"
        else -> "K"
    }

    val speed = when(units){
        Units().metric -> "km/h"
        Units().imperial -> "mil/h"
        else -> "met/s"
    }
    val weatherIcon : ImageVector = when (data.weather.firstOrNull()?.main) {
        "Clear" -> Icons.Outlined.WbSunny
        "Clouds" -> Icons.Outlined.Cloud
        "Rain" -> ImageVector.vectorResource(R.drawable.rounded_rainy_24)
        "Snow" -> ImageVector.vectorResource(R.drawable.rounded_weather_snowy_24)
        "Thunderstorm" -> Icons.Outlined.Thunderstorm
        "Drizzle" -> Icons.Outlined.CloudQueue
        else -> ImageVector.vectorResource(R.drawable.outline_mist_24)
    }

    val textColor : Color = when (data.weather.firstOrNull()?.main) {
        "Clear" -> MaterialTheme.colorScheme.primaryContainer // Солнечно – теплый, жёлто-оранжевый
        "Clouds" -> MaterialTheme.colorScheme.onBackground // Облачно – сероватый оттенок
        "Rain" -> MaterialTheme.colorScheme.secondaryContainer // Дождь – сине-серый или голубоватый
        "Snow" -> MaterialTheme.colorScheme.tertiaryContainer // Снег – светло-голубой или белый
        "Thunderstorm" -> MaterialTheme.colorScheme.errorContainer // Гроза – тёмный фиолетовый или красноватый
        "Drizzle" -> MaterialTheme.colorScheme.surface // Морось – светло-серый
        else -> MaterialTheme.colorScheme.surfaceVariant // По умолчанию – нейтральный цвет
    }
    val pollingNames = stringArrayResource(R.array.air_purity_level)
    val pollingName = when(aqi[0].main.aqi){
        1 -> pollingNames[0]
        2 -> pollingNames[1]
        3 -> pollingNames[2]
        4 -> pollingNames[3]
        5 -> pollingNames[4]
        else -> pollingNames[5]
    }
    val nameColor =  when(aqi[0].main.aqi){
        1 -> AQI_Good
        2 -> AQI_Moderate
        3 -> AQI_UnhealthySensitive
        4 -> AQI_Unhealthy
        5 -> AQI_VeryUnhealthy
        else -> AQI_Neutral
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        Modifier.fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .padding(top = 135.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize(),
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(36.dp),
                    colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .clickable {}
                        .padding(26.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row {
                            Text(
                                "$temperature ${data.main.temp} ",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Icon(
                                imageVector = weatherIcon,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                        Spacer(Modifier.padding(vertical = 8.dp))
                        Text(
                            data.weather[0].description,
                            style = MaterialTheme.typography.displaySmall,
                            color = textColor,
                        )
                        Text(
                            "${stringResource(R.string.feels_like)} $temperature ${data.main.feels_like}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if(data.rain != null){
                            Text(
                                "${arr[0]} ${data.rain.speed} $speed",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        if(data.snow != null){
                            Text(
                                "${arr[1]} ${data.snow.speed} $speed",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        Row {
                            Text(
                                text = "AQI: ${aqi.first().main.aqi} ",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                pollingName,
                                color = nameColor,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                    }
                }
                Spacer(Modifier.padding(vertical = 8.dp))
                if (forecastList != null) {
                TwoDayForecastCard(weatherIcon, temperature, forecastList, timeZone)
                FiveDayForecastCard(forecastList, timeZone, language,temperature)
                }
                if (forecastCity != null) {
                    MDCard(data, speed, temperature, aqi[0].components, forecastCity, timeZone)
                }
            }
        }
    }
}