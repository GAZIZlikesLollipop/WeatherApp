package com.example.weatherapp.pressentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AirComponents
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastCity
import com.google.accompanist.flowlayout.FlowRow

data class MDCardItem(
    val icon: ImageVector,
    val name: String,
    val info: String,
    val content: @Composable (() -> Unit)? = null
)
@Composable
fun MDCard(data: CurrentWeatherResponse, speed: String, temperature: String, pur : AirComponents, city : ForecastCity, timeZone : String) {
    val list = stringArrayResource(R.array.more_dt)
    val arr = stringArrayResource(R.array.mdcard)
    val visibility = if(data.main.visibility != null) "${data.main.visibility} km" else stringResource(R.string.vis)
    val wind : String = if(data.wind.gust != null) "${arr[4]}: ${data.wind.speed}\n${arr[5]}: ${data.wind.gust}" else "${arr[4]}: ${data.wind.speed}"
    val temp = if(data.main.maxTemp == data.main.minTemp) "${arr[1]}: ${data.main.maxTemp}" else "${arr[6]}: ${data.main.maxTemp}°\n${arr[7]}: ${data.main.minTemp}°"
    val items = listOf(
        MDCardItem(
            icon = ImageVector.vectorResource(R.drawable.rounded_thermostat_24),
            name = "${list[0]} $temperature",
            info = temp
        ),
        MDCardItem(
            icon = Icons.Default.Compress,
            name = list[1],
            info = "${arr[2]}: ${data.main.ground} hPa\n ${arr[3]}: ${data.main.pressure}"
        ),
        MDCardItem(
            icon = Icons.Rounded.WaterDrop,
            name = list[2],
            info = "${data.main.humidity} %"
        ),
        MDCardItem(
            icon = Icons.Default.Air,
            name = "${list[4]} $speed",
            info = wind,
            content = {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .size(26.dp)
                        .rotate(data.wind.deg.toFloat())
                        .offset(x = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        ),
        MDCardItem(
            icon = Icons.Default.FilterDrama,
            name = list[5],
            info = "${data.clouds.all}%"
        ),
        MDCardItem(
            icon = Icons.Default.Visibility,
            name = list[3],
            info = visibility
        ),
    )
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        mainAxisSpacing = 6.dp, // Расстояние между колонками
        crossAxisSpacing = 6.dp, // Расстояние между строками
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { }
        ) {
            val sun = stringArrayResource(R.array.sun)
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight()) {
                Row{
                    Icon(
                        imageVector = Icons.Rounded.WbTwilight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        sun[0],
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    "${sun[1]}: ${convertLongToHours(city.sunrise, timeZone)}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "${sun[2]}: ${convertLongToHours(city.sunset, timeZone)}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        items.forEach { item ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .width(160.dp)
                    .height(100.dp)
                    .clickable { }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Row {
                        Text(
                            text = item.info,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        if(item.content != null){
                            item.content.invoke()
                        }
                    }
                }
            }
        }
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { }
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight()) {
                Row{
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.rounded_aq_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .offset(y = (-8).dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        stringResource(R.string.airPollution),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                val pN = stringArrayResource(R.array.air_purity_names)
                Column {
                    Text("${pN[0]} ${pur.co} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[1]} ${pur.no2} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[2]} ${pur.o3} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[3]} ${pur.so2} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[4]} ${pur.pm25} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[5]} ${pur.pm10} μg/m3")
                    Spacer(Modifier.padding(vertical = 1.dp))
                    Text("${pN[6]} ${pur.nh3} μg/m3")
                }
            }
        }
    }
}