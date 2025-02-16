package com.example.weatherapp.pressentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.pressentation.main.WeatherViewModel


@Composable
fun ErrorScreen(viewModel: WeatherViewModel) {
    val retryLabel = stringResource(R.string.try_again)
    val errorMessage = stringResource(R.string.error)
    Box{
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center, // Центрируем по вертикали
            horizontalAlignment = Alignment.CenterHorizontally // Центрируем по горизонтали,
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.offset(x = 35.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.fetchCurrentWeather()
                    viewModel.fetchWeatherForecast()
                          },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = retryLabel,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun Loading(){
    CircularProgressIndicator(
        modifier = Modifier
            .size(100.dp)
            .offset(y = 300.dp, x = 150.dp)
    )
}