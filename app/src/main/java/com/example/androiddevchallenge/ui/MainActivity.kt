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
package com.example.androiddevchallenge.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.androiddevchallenge.domain.ContentState.SIMPLE
import com.example.androiddevchallenge.domain.WeatherForecastState.LOADING
import com.example.androiddevchallenge.domain.WeatherUnit.METRIC
import com.example.androiddevchallenge.ui.components.background.WeatherForecastSurface
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContent
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBar
import com.example.androiddevchallenge.ui.theme.WeatherForecastTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@AndroidEntryPoint
class MainActivity : ForecastActivity() {

    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherForecastTheme {
                ProvideWindowInsets {
                    WeatherForecast(
                        forecastViewModel = viewModel,
                        onSetMyLocationClick = {
                            getLocation()
                        }
                    )
                }
            }
        }

        getLocation()
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun WeatherForecast(forecastViewModel: ForecastViewModel, onSetMyLocationClick: () -> Unit) {
    val state by forecastViewModel.state.observeAsState(LOADING)
    val contentState by forecastViewModel.contentState.observeAsState(SIMPLE)
    val weatherUnit by forecastViewModel.weatherUnit.observeAsState(METRIC)

    WeatherForecastSurface(viewModel = forecastViewModel) {
        WeatherForecastTopBar(
            viewModel = forecastViewModel,
            state = state,
            contentState = contentState,
            weatherUnit = weatherUnit,
            onSetMyLocationClick = onSetMyLocationClick
        )
        WeatherForecastContent(
            viewModel = forecastViewModel,
            state = state,
            contentState = contentState,
            weatherUnit = weatherUnit
        )
    }
}
