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
package com.example.androiddevchallenge.ui.components.topbar

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.ContentState
import com.example.androiddevchallenge.domain.ContentState.DETAILED
import com.example.androiddevchallenge.domain.ContentState.SIMPLE
import com.example.androiddevchallenge.domain.WeatherForecastState
import com.example.androiddevchallenge.domain.WeatherForecastState.IDLE
import com.example.androiddevchallenge.domain.WeatherForecastState.RUNNING
import com.example.androiddevchallenge.domain.WeatherUnit
import com.example.androiddevchallenge.domain.WeatherUnit.IMPERIAL
import com.example.androiddevchallenge.domain.WeatherUnit.METRIC
import com.example.androiddevchallenge.ui.ForecastViewModel
import com.example.androiddevchallenge.ui.components.content.AnimationDuration
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBarTestHelper.GetLocationButton
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBarTestHelper.SearchBar
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBarTestHelper.SearchBarClearButton
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBarTestHelper.ViewTypeToggle
import com.example.androiddevchallenge.ui.components.topbar.WeatherForecastTopBarTestHelper.WeatherUnitToggle
import com.example.androiddevchallenge.ui.theme.BigDimension
import com.example.androiddevchallenge.ui.theme.MediumDimension
import com.example.androiddevchallenge.ui.theme.SmallDimension
import dev.chrisbanes.accompanist.insets.statusBarsHeight

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun WeatherForecastTopBar(
    viewModel: ForecastViewModel,
    state: WeatherForecastState,
    contentState: ContentState,
    weatherUnit: WeatherUnit,
    onSetMyLocationClick: () -> Unit
) {
    val cities by viewModel.cities.observeAsState(initial = listOf())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .statusBarsHeight()
            .padding(MediumDimension),
        verticalArrangement = Arrangement.spacedBy(BigDimension),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoBar(
            viewModel = viewModel,
            state = state,
            contentState = contentState,
            weatherUnit = weatherUnit,
            onSetMyLocationClick = onSetMyLocationClick
        )

        WeatherSearchBar(
            modifier = Modifier.setTestTag(SearchBar),
            trailingIconModifier = Modifier.setTestTag(SearchBarClearButton),
            viewModel = viewModel,
            cities = cities,
            state = state
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun LogoBar(
    viewModel: ForecastViewModel,
    state: WeatherForecastState,
    contentState: ContentState,
    weatherUnit: WeatherUnit,
    onSetMyLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = SmallDimension, end = SmallDimension),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.subtitle1
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(SmallDimension),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherUnitToggle(
                viewModel = viewModel,
                isActive = state == RUNNING,
                weatherUnit = weatherUnit
            )

            ViewTypeToggle(
                viewModel = viewModel,
                isActive = state == RUNNING,
                contentState = contentState
            )

            GetLocationButton(
                isActive = state == RUNNING || state == IDLE,
                onSetMyLocationClick = onSetMyLocationClick
            )
        }
    }
}

@Composable
fun WeatherUnitToggle(viewModel: ForecastViewModel, isActive: Boolean, weatherUnit: WeatherUnit) {
    val buttonTransition = updateTransition(targetState = isActive, label = "")
    val buttonAlpha by topBarButtonTransition(transition = buttonTransition)
    IconButton(
        modifier = Modifier
            .alpha(buttonAlpha)
            .setTestTag(WeatherUnitToggle),
        onClick = {
            viewModel.setWeatherUnit(if (weatherUnit == METRIC) METRIC else IMPERIAL)
        },
        enabled = isActive
    ) {
        Icon(
            painter = painterResource(
                id =
                if (weatherUnit == METRIC) R.drawable.centigrade
                else R.drawable.fahrenheit
            ),
            contentDescription = stringResource(id = R.string.get_my_location),
            modifier = Modifier.requiredSize(BigDimension)
        )
    }
}

@Composable
fun ViewTypeToggle(viewModel: ForecastViewModel, isActive: Boolean, contentState: ContentState) {
    val buttonTransition = updateTransition(targetState = isActive, label = "")
    val buttonAlpha by topBarButtonTransition(transition = buttonTransition)

    IconToggleButton(
        modifier = Modifier
            .alpha(buttonAlpha)
            .setTestTag(ViewTypeToggle),
        checked = contentState == DETAILED,
        onCheckedChange = { viewModel.setContentState(if (contentState == SIMPLE) DETAILED else SIMPLE) },
        enabled = isActive
    ) {
        val icon = if (contentState == DETAILED) R.drawable.ic_list else R.drawable.detailed_view
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.toggle_view_type),
            modifier = Modifier.requiredSize(BigDimension)
        )
    }
}

@Composable
fun GetLocationButton(isActive: Boolean, onSetMyLocationClick: () -> Unit) {
    val buttonTransition = updateTransition(targetState = isActive, label = "")
    val buttonAlpha by topBarButtonTransition(transition = buttonTransition)

    IconButton(
        modifier = Modifier
            .alpha(buttonAlpha)
            .setTestTag(GetLocationButton),
        onClick = { onSetMyLocationClick() },
        enabled = isActive
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_my_location),
            contentDescription = stringResource(
                id = R.string.get_my_location
            )
        )
    }
}

@Composable
fun topBarButtonTransition(transition: Transition<Boolean>): State<Float> {
    return transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = AnimationDuration / 4, easing = FastOutSlowInEasing)
        },
        label = ""
    ) { isActive ->
        when (isActive) {
            true -> 1f
            false -> 0f
        }
    }
}

object WeatherForecastTopBarTestHelper {
    fun getTestTag(viewTag: String) = "WeatherForecastTopBar_$viewTag"

    const val SearchBar = "SearchBar"
    const val SearchBarClearButton = "SearchBarClearButton"
    const val GetLocationButton = "GetLocationButton"
    const val WeatherUnitToggle = "WeatherUnitToggle"
    const val ViewTypeToggle = "ViewTypeToggle"
}

private fun Modifier.setTestTag(tag: String): Modifier = composed {
    semantics {
        testTag = WeatherForecastTopBarTestHelper.getTestTag(tag)
    }
}
