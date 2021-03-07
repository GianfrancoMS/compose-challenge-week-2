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

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.TimerViewModel.Companion.DEFAULT_START_TIME
import com.example.androiddevchallenge.ui.theme.ChallengeTheme
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ChallengeTheme {
                ProvideWindowInsets {
                    ChallengeApp()
                }
            }
        }
    }
}

// Start building your app here!
@Composable
fun ChallengeApp() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        val viewModel: TimerViewModel = viewModel()
        val time by viewModel.timeInSeconds.observeAsState(DEFAULT_START_TIME)
        val progress by viewModel.progress.observeAsState(1f)

        val animatedProgress = remember { Animatable(progress) }

        LaunchedEffect(viewModel) {
            viewModel.startTimer()
        }

        LaunchedEffect(time) {
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }

        WaterWave(
            modifier = Modifier.align(Alignment.BottomCenter),
            progress = animatedProgress.value
        )
        Timer(
            time = DateUtils.formatElapsedTime(time),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
    }
}
