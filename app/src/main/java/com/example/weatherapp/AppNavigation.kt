package com.example.weatherapp

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.pressentation.main.WeatherViewModel
import com.example.weatherapp.pressentation.main.HomeScreen
import com.example.weatherapp.pressentation.main.SettingsScreen

@Composable
fun AppNavigation(){
    val navControlller = rememberNavController()
    val viewModel : WeatherViewModel = viewModel()
    NavHost(navControlller, startDestination = "home"){
        composable(
            "home",
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500))
            }
        ) {
            HomeScreen(navControlller, viewModel)
        }
        composable(
            "settings",
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(550))
            }
            ) {
            SettingsScreen(navControlller,viewModel)
        }
    }
}