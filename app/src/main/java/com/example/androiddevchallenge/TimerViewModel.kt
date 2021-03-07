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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var timerJob: Job? = null

    private val _timeInSeconds = MutableLiveData(DEFAULT_START_TIME)
    val timeInSeconds: LiveData<Long> get() = _timeInSeconds

    val progress: LiveData<Float> = timeInSeconds.map { time ->
        time / DEFAULT_START_TIME.toFloat()
    }

    fun startTimer() {
        stopTimer()

        if (getCurrentTime() <= 0) {
            startIncreaseTimer()
        } else {
            startDecreaseTimer()
        }
    }

    private fun startIncreaseTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(ONE_SECOND)
                val newTime = getCurrentTime() + 1
                _timeInSeconds.value = newTime

                if (newTime >= DEFAULT_START_TIME) {
                    startTimer()
                    break
                }
            }
        }
    }

    private fun startDecreaseTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(ONE_SECOND)
                val newTime = getCurrentTime() - 1
                _timeInSeconds.value = newTime

                if (newTime <= 0) {
                    startTimer()
                    break
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun getCurrentTime(): Long {
        return _timeInSeconds.value ?: DEFAULT_START_TIME
    }

    companion object {
        const val DEFAULT_START_TIME = 30L
        private const val ONE_SECOND = 1000L
    }
}
