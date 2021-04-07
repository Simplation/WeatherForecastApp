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
package com.example.androiddevchallenge.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.data.forecast.FakeForecastDao.Companion.MaxPrecipitation
import com.example.androiddevchallenge.data.forecast.FakeForecastDao.Companion.MaxTemperature
import com.example.androiddevchallenge.data.forecast.FakeForecastDao.Companion.MaxWindSpeed
import org.joda.time.LocalDateTime

data class DailyForecast(
    val timestamp: String,
    val hourlyForecasts: List<HourlyForecast> = listOf(),
    val temperature: Int,
    val minTemperature: Int,
    val maxTemperature: Int,
    val precipitationProbability: Int,
    val windSpeed: Int,
    val weather: Weather
) : IDailyForecast {

    override fun generateWeatherColorFeel() =
        Color(
            red = (temperature * 255 / MaxTemperature.toFloat()) / 255f,
            green = (windSpeed * 255 / MaxWindSpeed.toFloat()) / 255f,
            blue = (precipitationProbability * 255 / MaxPrecipitation.toFloat()) / 255f
        )
}

@Composable
fun DailyForecast.getFormattedTime(): String {
    val timestampTime = LocalDateTime.parse(timestamp)
    val today = LocalDateTime.now()
    return when {
        timestampTime.dayOfYear == today.dayOfYear && timestampTime.year == today.year -> stringResource(
            R.string.today
        )
        timestampTime.dayOfYear == today.plusDays(1).dayOfYear && timestampTime.year == today.plusDays(
            1
        ).year -> stringResource(
            R.string.tomorrow
        )
        else -> timestampTime.toString(Forecast.DailyTimestampFormat)
    }
}
