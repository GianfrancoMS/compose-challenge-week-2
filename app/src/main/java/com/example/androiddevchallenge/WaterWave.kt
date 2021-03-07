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
package com.example.androiddevchallenge

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.pow
import kotlin.math.sin

private const val X_DELTA = 0.01
private const val PI = kotlin.math.PI
private const val AMPLITUDE = 0.4
private const val PERIOD = PI

private val WATER_COLOR = Color(red = 116, green = 191, blue = 249)

/*
* Sine function is defined as:
*   Y = A sin (B (x + C) ) + D
* Where:
* A = amplitude
* B = period (2pi / B)
* C = phase shift (horizontal shift)
* D = vertical shift
* However, since we want only positive values,
* we are going to use sin ^ 2 (x)
* */

@Composable
fun WaterWave(modifier: Modifier = Modifier, progress: Float) {
    if (progress <= 0.0f) {
        return
    }

    val infiniteTransition = rememberInfiniteTransition()
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (PERIOD).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween((60 / (PERIOD * X_DELTA)).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val path = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(progress)
    ) {
        val (canvasWidth, canvasHeight) = size
        val widthScaleFactor = canvasWidth / PERIOD
        val heightScaleFactor = minOf(canvasHeight / 1.5, widthScaleFactor)

        var firstY = 0.0f
        var lastX = 0.0f

        path.reset()
        generateSequence(0.0, { it + X_DELTA })
            .takeWhile { it <= PERIOD }
            .forEach { x ->
                val y = AMPLITUDE * sin(x + shift).pow(2)
                val realX = (x * widthScaleFactor).toFloat()
                val realY = (y * heightScaleFactor).toFloat()
                if (x == 0.0) {
                    path.moveTo(realX, realY)
                    firstY = realY
                } else {
                    path.lineTo(realX, realY)
                    lastX = realX
                }
            }

        path.lineTo(lastX, canvasHeight)
        path.lineTo(0f, canvasHeight)
        path.lineTo(0f, firstY)

        drawPath(
            path = path,
            color = WATER_COLOR,
            style = Fill
        )
    }
}

@Preview("Water Wave", widthDp = 360, heightDp = 640)
@Composable
fun WaterWavePreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        WaterWave(modifier = Modifier.align(Alignment.BottomCenter), progress = 0.5f)
    }
}
