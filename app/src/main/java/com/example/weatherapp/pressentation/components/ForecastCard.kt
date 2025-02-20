package com.example.weatherapp.pressentation.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.ForecastList
import java.text.SimpleDateFormat
import java.util.*

data class DayForecastData(
    val time: String,
    val icon: ImageVector,
    val temperature: Double,
    val rainChance: Double
)

@SuppressLint("StateFlowValueCalledInComposition", "SuspiciousIndentation")
@Composable
fun TwoDayForecastCard(icon: ImageVector, temp: String, list : List<ForecastList>, timeZone: String) {
    val items = list.map { item ->
        DayForecastData(
            time = convertLongToHours(item.dt, timeZone),
            icon = icon,
            temperature = item.main.temp,
            rainChance = item.pop
        )
    }
    val nextDay = getNextDay(list[1].dt, timeZone)

    Box(
        Modifier
            .height(150.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.05f))
    ) {

        LazyRow(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
            itemsIndexed(items.take(9)) { index, item ->

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val itemTime = timeFormat.parse(item.time) // Преобразуем строку в Date
                val noon = timeFormat.parse("02:00")

                Column {
                    val text: String = if (index == 0) {
                        stringArrayResource(R.array.mdcard)[0]
                    } else if (itemTime == noon) {
                        "${nextDay}D ${item.time}"
                    } else {
                        item.time
                    }
                    val offset = if (itemTime == noon) -3 else 10
                    val style = if (itemTime == noon) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge
                    Text(
                        text,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                        style = style,
                        modifier = Modifier.offset(x = offset.dp)
                    )
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp)
                            .offset(x = 12.dp)
                    )
                    Text(
                        "$temp ${item.temperature}",
                        fontSize = 20.sp
                    )
                    Row {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.rounded_humidity_percentage_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "${item.rainChance}%",
                            fontSize = 21.sp,
                            modifier = Modifier.offset(y = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.padding(horizontal = 12.dp))
            }
        }
    }
    Spacer(Modifier.padding(vertical = 12.dp))
}

fun convertLongToHours(timestamp: Long, timeZoneId: String): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone(timeZoneId)
    return format.format(date)
}

fun getNextDay(timestamp: Long, timeZoneId: String): Int {
    val date = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))
    date.timeInMillis = timestamp * 1000
    return date.get(Calendar.DAY_OF_MONTH) + 1 // Возвращаем день месяца как Int
}


data class FiveForecastData(
    val date: String,
    val humidity: Int,
    val icon : Int,
    val minTemp: Double,
    val maxTemp: Double
)
@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FiveDayForecastCard(list: List<ForecastList>, timeZone: String, lang: String,temp: String){

    val context = LocalContext.current

    val data = list.map { item ->
        FiveForecastData(
            date = convertTSToDM(item.dt, timeZone, lang),
            humidity = item.main.humidity,
            icon = getWeatherIcon(context,"_${item.weather.first().icon}"),
            minTemp = item.main.maxTemp,
            maxTemp = item.main.minTemp
        )
    }

    Card(
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)),
        modifier = Modifier
            .clickable {}
            .padding(16.dp)
            .clip(RoundedCornerShape(36.dp))
            .fillMaxSize()
    ) {
        FlowColumn(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxSize()
        ) {
            Row(Modifier.fillMaxWidth()){
                stringArrayResource(R.array.forecast_info).forEach{info ->
                    Text(
                        info,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.67f)
                    )
                    Spacer(Modifier.padding(horizontal = 8.dp))
                }
            }
            Spacer(Modifier.padding(vertical = 10.dp))
            val uniqueData = data.distinctBy { it.date }.take(6)
            uniqueData.forEach { dt ->
                val dayIconRes = if (dt.icon != 0) dt.icon else R.drawable._04d
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        dt.date,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.padding(horizontal = 8.dp))
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.padding(horizontal = 3.dp))
                    Text(
                        "${dt.humidity}%",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.padding(horizontal = 12.dp))
                    Image(
                        painter = painterResource(dayIconRes),
                        contentDescription = null
                    )

                    Spacer(Modifier.padding(horizontal = 12.dp))
                    if(dt.maxTemp == dt.minTemp) {
                        Text(
                            "$temp${dt.maxTemp}  $temp${dt.minTemp}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }else{
                        Text(
                            "$temp${dt.maxTemp}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(Modifier.padding(vertical = 3.dp))
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
fun getWeatherIcon(context: Context, iconName: String): Int {
    val id = context.resources.getIdentifier(iconName, "drawable", context.packageName)
    return if (id != 0) id else R.drawable._03d // Укажи резервную иконку
}

fun convertTSToDM(timestamp: Long, timeZoneId: String, lang : String): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("d MMM", Locale(lang))
    format.timeZone = TimeZone.getTimeZone(timeZoneId)
    return format.format(date)
}