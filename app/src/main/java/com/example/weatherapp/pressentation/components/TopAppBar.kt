@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.weatherapp.pressentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.pressentation.main.WeatherViewModel

@Composable
fun HomeTopBar(
    navController: NavController,
    viewModel: WeatherViewModel
){

    val topBarColor = Color(0x66000000) // Полупрозрачный черный (стеклянный эффект)

    TopAppBar(
        title = {
            Row(Modifier.fillMaxWidth()){
                Text(
                    text = "${stringResource(R.string.weather)} ${viewModel.city}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(2f)
                )
                IconButton(
                    onClick = {navController.navigate("settings")}
                ){
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Navigate to settings screen",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor.copy(alpha = 0.15f)),
    )
}

@Composable
fun SettingsTopBar(navController: NavController){
    TopAppBar(
        title = {
            Row(Modifier.fillMaxWidth()){
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.weight(1f)
                ){
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(3f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.85f))
    )
}