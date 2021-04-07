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
package com.example.androiddevchallenge.ui.components.content.detailed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.WeatherUnit
import com.example.androiddevchallenge.domain.model.DailyForecast
import com.example.androiddevchallenge.domain.model.getFormattedTime
import com.example.androiddevchallenge.ui.components.content.UnselectedAlpha
import com.example.androiddevchallenge.ui.components.utils.WeatherAnimation
import com.example.androiddevchallenge.ui.components.utils.WeatherTemperature
import com.example.androiddevchallenge.ui.components.utils.WeatherWindAndPrecipitation
import com.example.androiddevchallenge.ui.theme.BigDimension
import com.example.androiddevchallenge.ui.theme.MediumDimension
import com.example.androiddevchallenge.ui.theme.SmallDimension

@Composable
fun DetailedContentDays(
    modifier: Modifier = Modifier,
    dailyForecasts: List<DailyForecast>,
    selectedDailyForecast: DailyForecast?,
    onDailyForecastSelected: (Int, DailyForecast) -> Unit,
    weatherUnit: WeatherUnit,
    dailyForecastsScrollState: LazyListState
) {
    val backgroundColor =
        (selectedDailyForecast?.generateWeatherColorFeel() ?: MaterialTheme.colors.primary).copy(
            alpha = UnselectedAlpha
        )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(
                MediumDimension
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                MediumDimension
            ),
            state = dailyForecastsScrollState
        ) {
            itemsIndexed(dailyForecasts) { index, dailyForecast ->
                Day(
                    isSelected = dailyForecast == selectedDailyForecast,
                    dailyForecast = dailyForecast,
                    backgroundColor = backgroundColor,
                    weatherUnit = weatherUnit,
                    onDailyForecastSelected = { onDailyForecastSelected(index, dailyForecast) }
                )
            }
        }
    }
}

@Composable
fun Day(
    isSelected: Boolean,
    dailyForecast: DailyForecast,
    backgroundColor: Color,
    weatherUnit: WeatherUnit,
    onDailyForecastSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor.copy(alpha = if (isSelected) UnselectedAlpha else 0f))
            .clickable { onDailyForecastSelected() }
            .padding(SmallDimension),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Description(dailyForecast = dailyForecast)
        ExtraInformation(dailyForecast = dailyForecast)
        WeatherTemperature(
            temperature = dailyForecast.temperature,
            minTemperature = dailyForecast.minTemperature,
            maxTemperature = dailyForecast.maxTemperature,
            temperatureStyle = MaterialTheme.typography.subtitle2,
            maxAndMinStyle = MaterialTheme.typography.body2,
            alignment = Alignment.Top,
            weatherUnit = weatherUnit
        )
    }
}

@Composable
fun ExtraInformation(dailyForecast: DailyForecast) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            SmallDimension
        )
    ) {
        WeatherWindAndPrecipitation(
            text = "${dailyForecast.windSpeed} km/h",
            icon = painterResource(id = R.drawable.ic_wind),
            contentDescription = stringResource(id = R.string.wind)
        )
        WeatherWindAndPrecipitation(
            text = "${dailyForecast.precipitationProbability} %",
            icon = painterResource(id = R.drawable.ic_drop),
            contentDescription = stringResource(
                id = R.string.precipitation_probability
            )
        )
    }
}

@Composable
fun Description(dailyForecast: DailyForecast) {
    Row(horizontalArrangement = Arrangement.spacedBy(SmallDimension)) {
        WeatherAnimation(weather = dailyForecast.weather, animationSize = BigDimension)
        Text(text = dailyForecast.getFormattedTime(), style = MaterialTheme.typography.subtitle2)
    }
}
