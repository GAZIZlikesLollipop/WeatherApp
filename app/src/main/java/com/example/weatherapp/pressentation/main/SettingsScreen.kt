package com.example.weatherapp.pressentation.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Units
import com.example.weatherapp.pressentation.components.SettingsTopBar
import java.util.Locale

data class UnitBuuton(
    val unit : String,
    val tempUn : String,
    val speedUn : String
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: WeatherViewModel
){
    LaunchedEffect(viewModel.units, viewModel.city) {
        viewModel.fetchCurrentWeather()
        viewModel.fetchWeatherForecast()
        viewModel.fetchAirPollution()
        viewModel.fetchTimeZone()
    }
    val context = LocalContext.current
    var cityChoose : Boolean by rememberSaveable{ mutableStateOf(false)}
    val items = listOf(
        UnitBuuton(
            unit = Units().standard,
            tempUn = "K",
            speedUn = "met/s"
        ),
        UnitBuuton(
            unit = Units().metric,
            tempUn = "C°",
            speedUn = "km/h"
        ),
        UnitBuuton(
            unit = Units().imperial,
            tempUn = "F°",
            speedUn = "mil/s"
        )
    )
    Scaffold(
        topBar = { SettingsTopBar(navController)},
        content = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp)){
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ){
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.units),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                        Row {
                            items.forEach{ item ->
                                val color = if(item.unit == viewModel.units){
                                    MaterialTheme.colorScheme.secondaryContainer
                                }else{
                                    MaterialTheme.colorScheme.surface
                                }
                                Button(
                                    onClick = {
                                        viewModel.saveUnit(item.unit)
                                    },
                                    shape = RoundedCornerShape(0.dp),
                                    colors = ButtonDefaults.buttonColors(color)
                                ) {
                                    Text(
                                        "${item.tempUn}, ${item.speedUn}",
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Spacer(Modifier.padding(horizontal = 2.dp))
                            }
                        }
                    }
                }
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            cityChoose = !cityChoose
                        },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.city),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                        Text(
                            "${stringResource(R.string.current)} ${viewModel.city}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            setAppLocale(context, if(viewModel.language == "ru"){
                                viewModel.language = "en"
                                viewModel.language
                            }else{viewModel.language = "ru"
                            viewModel.language})
                        },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringArrayResource(R.array.lang)[0],
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                        )
                        Text(
                            "${stringResource(R.string.current)} ${viewModel.language}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            if(cityChoose){
                Card(
                    Modifier
                        .padding(16.dp)
                        .padding(top = 150.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(36.dp)
                ){
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)){
                        Row(Modifier.height(125.dp)){
                            Text(
                                "Cities (${stringResource(R.string.current)} ${viewModel.city})",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.weight(2f)
                            )

                            Button(
                                onClick = {
                                    cityChoose = false
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                            ){
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.background
                                )
                            }
                        }
                        val uzb_cities = stringArrayResource(R.array.uzb_cities)
                        val contry_capitals = stringArrayResource(R.array.capitals)
                        LazyColumn {
                            items(uzb_cities){ text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.saveCity(text)
                                            cityChoose = false
                                        }
                                ){
                                    Text(text, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground,modifier = Modifier.weight(2f))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                            }

                            items(contry_capitals){text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.saveCity(text)
                                            cityChoose = false
                                        }
                                ){
                                    Text(text, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground,modifier = Modifier.weight(2f))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    )
}

fun setAppLocale(context: Context, language: String) {

    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration()
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}