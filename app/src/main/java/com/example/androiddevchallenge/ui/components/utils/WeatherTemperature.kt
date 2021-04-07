/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.components.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.WeatherUnit
import kotlin.math.roundToInt

@Composable
fun WeatherTemperature(
    temperature: Int,
    minTemperature: Int? = null,
    maxTemperature: Int? = null,
    weatherUnit: WeatherUnit,
    compressUnitOnAverageTemp: Boolean = true,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    temperatureStyle: TextStyle = MaterialTheme.typography.h1,
    maxAndMinStyle: TextStyle = MaterialTheme.typography.subtitle2
) {
    val finalTemperature = when (weatherUnit) {
        WeatherUnit.METRIC -> temperature
        WeatherUnit.IMPERIAL -> ((temperature * (9 / 5f)) + 32).roundToInt()
    }

    val finalMinTemperature = minTemperature?.let {
        when (weatherUnit) {
            WeatherUnit.METRIC -> minTemperature
            WeatherUnit.IMPERIAL -> ((minTemperature * (9 / 5f)) + 32).roundToInt()
        }
    }

    val finalMaxTemperature = maxTemperature?.let {
        when (weatherUnit) {
            WeatherUnit.METRIC -> maxTemperature
            WeatherUnit.IMPERIAL -> ((maxTemperature * (9 / 5f)) + 32).roundToInt()
        }
    }

    Row(verticalAlignment = alignment) {
        AverageTemperature(
            temperature = finalTemperature,
            weatherUnit = weatherUnit,
            style = temperatureStyle,
            compressUnitOnAverageTemp = compressUnitOnAverageTemp
        )
        MinAndMaxTemperature(
            minTemperature = finalMinTemperature,
            maxTemperature = finalMaxTemperature,
            weatherUnit = weatherUnit,
            style = maxAndMinStyle
        )
    }
}

@Composable
private fun AverageTemperature(
    temperature: Int,
    weatherUnit: WeatherUnit,
    style: TextStyle,
    compressUnitOnAverageTemp: Boolean
) {
    Text(
        text = "$temperature${if (compressUnitOnAverageTemp) weatherUnit.compressedIndication else weatherUnit.normalIndication}",
        style = style
    )
}

@Composable
private fun MinAndMaxTemperature(
    minTemperature: Int?,
    maxTemperature: Int?,
    weatherUnit: WeatherUnit,
    style: TextStyle
) {
    Column(modifier = Modifier.padding(top = 1.dp)) {
        if (minTemperature != null && maxTemperature != null) {
            MaxAndMaxTemperatureItem(
                temperature = maxTemperature,
                weatherUnit = weatherUnit,
                style = style,
                icon = painterResource(id = R.drawable.ic_arrow_up),
                contentDescription = stringResource(id = R.string.max_temperature)
            )
            MaxAndMaxTemperatureItem(
                temperature = minTemperature,
                weatherUnit = weatherUnit,
                style = style,
                icon = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = stringResource(id = R.string.min_temperature)
            )
        }
    }
    Text(text = "", style = style)
}

@Composable
private fun MaxAndMaxTemperatureItem(
    temperature: Int,
    weatherUnit: WeatherUnit,
    style: TextStyle,
    icon: Painter,
    contentDescription: String?
) {
    Row {
        Icon(painter = icon, contentDescription = contentDescription)
        Text(text = "$temperature ${weatherUnit.normalIndication}", style = style)
    }
}
