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
package com.example.androiddevchallenge.ui.components.content.simple

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.example.androiddevchallenge.domain.ContentState
import com.example.androiddevchallenge.domain.WeatherUnit
import com.example.androiddevchallenge.domain.model.DailyForecast
import com.example.androiddevchallenge.domain.model.Forecast
import com.example.androiddevchallenge.ui.ForecastViewModel
import com.example.androiddevchallenge.ui.components.content.AnimationDuration
import com.example.androiddevchallenge.ui.components.content.contentOffsetTransition
import com.example.androiddevchallenge.ui.theme.BigDimension
import com.example.androiddevchallenge.ui.theme.MediumDimension
import com.example.androiddevchallenge.utils.scrollToBegin
import kotlinx.coroutines.launch

@Composable
fun WeatherForecastSimpleContent(
    modifier: Modifier = Modifier,
    viewModel: ForecastViewModel,
    isActive: Boolean,
    forecast: Forecast?,
    selectedDailyForecast: DailyForecast?,
    weatherUnit: WeatherUnit
) {
    val coroutineScope = rememberCoroutineScope()
    val dailyForecastsScrollState = rememberLazyListState()
    val hourlyForecastsScrollState = rememberLazyListState()

    val transition = updateTransition(targetState = isActive, label = "")

    if (!isActive) {
        dailyForecastsScrollState.scrollToBegin(coroutineScope)
        hourlyForecastsScrollState.scrollToBegin(coroutineScope)
    }

    val firstTileValue by contentOffsetTransition(transition = transition)
    val secondTitleValue by contentOffsetTransition(transition = transition, delay = 100)
    val thirdTitleValue by contentOffsetTransition(transition = transition, delay = 200)
    val alphaValue by transition.animateFloat(
        transitionSpec = { tween(AnimationDuration) },
        label = ""
    ) { active -> if (active) 1f else 0f }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MediumDimension
        )
    ) {
        if (isActive || alphaValue > .15f) {
            SimpleContentDetails(
                modifier = modifier
                    .offset(x = firstTileValue)
                    .alpha(alphaValue),
                selectedDailyForecast = selectedDailyForecast,
                weatherUnit = weatherUnit
            )
            SimpleContentDays(
                modifier = modifier
                    .padding(top = BigDimension)
                    .offset(x = secondTitleValue)
                    .alpha(alphaValue),
                scrollState = dailyForecastsScrollState,
                selectedDailyForecast = selectedDailyForecast,
                dailyForecasts = forecast?.dailyForecasts?.take(2) ?: listOf(),
                onMoreClick = {
                    viewModel.setContentState(ContentState.DETAILED)
                },
                onDailyForecastSelected = { index, newSelectedDailyForecast ->
                    viewModel.selectDailyForecast(newSelectedDailyForecast)
                    coroutineScope.launch {
                        dailyForecastsScrollState.animateScrollToItem(index)
                        hourlyForecastsScrollState.animateScrollToItem(0)
                    }
                }
            )
            SimpleContentHours(
                modifier = modifier
                    .offset(x = thirdTitleValue)
                    .alpha(alphaValue),
                scrollState = hourlyForecastsScrollState,
                hourlyForecasts = selectedDailyForecast?.hourlyForecasts ?: listOf(),
                surfaceColor = selectedDailyForecast?.generateWeatherColorFeel(),
                weatherUnit = weatherUnit
            )
        }
    }
}
