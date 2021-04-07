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
package com.example.androiddevchallenge.ui.components.content

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.ContentState
import com.example.androiddevchallenge.domain.ContentState.DETAILED
import com.example.androiddevchallenge.domain.ContentState.SIMPLE
import com.example.androiddevchallenge.domain.WeatherForecastState
import com.example.androiddevchallenge.domain.WeatherForecastState.IDLE
import com.example.androiddevchallenge.domain.WeatherForecastState.LOADING
import com.example.androiddevchallenge.domain.WeatherForecastState.LocationError
import com.example.androiddevchallenge.domain.WeatherForecastState.RUNNING
import com.example.androiddevchallenge.domain.WeatherUnit
import com.example.androiddevchallenge.ui.ForecastViewModel
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContentTestHelper.DetailedContent
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContentTestHelper.DetectingLocation
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContentTestHelper.DetectingLocationError
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContentTestHelper.NoCity
import com.example.androiddevchallenge.ui.components.content.WeatherForecastContentTestHelper.SimpleContent
import com.example.androiddevchallenge.ui.components.content.detailed.WeatherForecastDetailedContent
import com.example.androiddevchallenge.ui.components.content.simple.WeatherForecastSimpleContent
import com.example.androiddevchallenge.ui.theme.BigDimension
import com.example.androiddevchallenge.ui.theme.MediumDimension
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

@ExperimentalAnimationApi
@Composable
fun WeatherForecastContent(
    viewModel: ForecastViewModel,
    state: WeatherForecastState,
    contentState: ContentState,
    weatherUnit: WeatherUnit
) {
    val forecast by viewModel.forecast.observeAsState()
    val selectedDailyForecast by viewModel.selectedDailyForecast.observeAsState()

    val stateTransition = updateTransition(targetState = state, label = "")

    val loadingValue by stateTransition.animateFloat(
        transitionSpec = { tween(AnimationDuration) },
        label = ""
    ) { newState ->
        if (newState == LOADING) 1f else 0f
    }

    val idleValue by stateTransition.animateFloat(
        transitionSpec = { tween(AnimationDuration) },
        label = ""
    ) { newState ->
        if (newState == IDLE) 1f else 0f
    }

    val locationErrorValue by stateTransition.animateFloat(
        transitionSpec = {
            tween(
                AnimationDuration
            )
        },
        label = ""
    ) { newState ->
        if (newState == LocationError) 1f else 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = MediumDimension, top = MediumDimension, end = MediumDimension)
            .navigationBarsPadding(left = false, right = false)
    ) {
        WeatherForecastSimpleContent(
            modifier = Modifier.setTestTag(SimpleContent),
            viewModel = viewModel,
            isActive = state == RUNNING && contentState == SIMPLE,
            forecast = forecast,
            selectedDailyForecast = selectedDailyForecast,
            weatherUnit = weatherUnit
        )

        WeatherForecastDetailedContent(
            modifier = Modifier.setTestTag(DetailedContent),
            viewModel = viewModel,
            isActive = state == RUNNING && contentState == DETAILED,
            forecast = forecast,
            selectedDailyForecast = selectedDailyForecast,
            weatherUnit = weatherUnit
        )

        Message(
            modifier = Modifier
                .scale(loadingValue)
                .alpha(loadingValue)
                .setTestTag(DetectingLocation),
            text = stringResource(id = R.string.detecting_location)
        )

        Message(
            modifier = Modifier
                .scale(locationErrorValue)
                .alpha(locationErrorValue)
                .setTestTag(DetectingLocationError),
            text = stringResource(id = R.string.location_error)
        )

        Message(
            modifier = Modifier
                .scale(idleValue)
                .alpha(idleValue)
                .setTestTag(NoCity),
            text = stringResource(id = R.string.no_city)
        )
    }
}

@Composable
private fun Message(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.subtitle1,
    align: TextAlign = TextAlign.Center
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = BigDimension),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = align,
            style = style
        )
    }
}

@Composable
fun contentOffsetTransition(
    transition: Transition<Boolean>,
    delay: Int = 0,
    inverseStart: Boolean = false
): State<Dp> {
    return transition.animateDp(
        transitionSpec = {
            tween(AnimationDuration, delayMillis = delay, easing = FastOutSlowInEasing)
        },
        label = ""
    ) { isActive ->
        when (isActive) {
            true -> AnimationEndOffset
            false -> if (inverseStart) AnimationStartOffset * -1 else AnimationStartOffset
        }
    }
}

object WeatherForecastContentTestHelper {
    fun getTestTag(viewTag: String) = "WeatherForecastContent_$viewTag"

    const val SimpleContent = "SimpleContent"
    const val DetailedContent = "DetailedContent"
    const val DetectingLocation = "DetectingLocation"
    const val DetectingLocationError = "DetectingLocationError"
    const val NoCity = "NoCity"
}

private fun Modifier.setTestTag(tag: String): Modifier = composed {
    semantics { testTag = WeatherForecastContentTestHelper.getTestTag(tag) }
}

const val SelectedAlpha = 0.25f
const val UnselectedAlpha = 0.1f
const val AnimationDuration = 1000
val AnimationStartOffset = 400.dp
val AnimationEndOffset = 0.dp
