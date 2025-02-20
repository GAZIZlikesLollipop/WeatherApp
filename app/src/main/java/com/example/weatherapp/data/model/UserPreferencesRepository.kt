package com.example.weatherapp.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

interface UserPreferences {
    suspend fun saveUserPreferences(city: String, language: String)
    suspend fun getUserPreferences(): Pair<String, String>
    suspend fun saveWeatherUnits(units : String)
    suspend fun getWeatherUnits(): String
}

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>): UserPreferences{
    private companion object{
        val CITY_KEY = stringPreferencesKey("city")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val UNITS_KEY = stringPreferencesKey("units")
    }

    override suspend fun saveUserPreferences(city: String, language: String) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[CITY_KEY] =  city
            mutablePreferences[LANGUAGE_KEY] = language
        }
    }

    override suspend fun getUserPreferences(): Pair<String, String> {
        val preferences = dataStore.data.first()
        val city = preferences[CITY_KEY] ?: "London"  // Значение по умолчанию
        val language = preferences[LANGUAGE_KEY] ?: "en" // Значение по умолчанию
        return Pair(city, language)
    }

    override suspend fun saveWeatherUnits(units: String) {
        dataStore.edit { preferences ->
            preferences[UNITS_KEY] = units
        }
    }

    override suspend fun getWeatherUnits(): String {
        val preferences = dataStore.data.first()
        val units = preferences[UNITS_KEY] ?: "standard"
        return units
    }
}
