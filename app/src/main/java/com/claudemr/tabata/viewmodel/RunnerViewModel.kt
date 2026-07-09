package com.claudemr.tabata.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.claudemr.tabata.audio.SpeechManager
import com.claudemr.tabata.model.*
import com.claudemr.tabata.repository.IWorkoutRepository
import com.claudemr.tabata.state.RunnerState

class RunnerViewModel(
    private val repository: IWorkoutRepository,
    private val speechManager: SpeechManager
) : ViewModel() {

    var state by mutableStateOf(RunnerState())
        private set

    private var timerJob: Job? = null

    fun initialize(workoutId: Long) = viewModelScope.launch {
        speechManager.awaitReady()
        startWorkout(workoutId)
    }

    fun startWorkout(workoutId: Long) {
        val workout = repository.workouts.value
            .firstOrNull { it.id == workoutId }
            ?: return

        state = RunnerState(
            workout = workout
        )

        viewModelScope.launch {
            speechManager.awaitReady()
            state = state.copy(
                isTtsReady = true
            )
            startCurrentStep()
        }
    }

    private fun startCurrentStep() {
        when (val step = state.currentStep) {
            is WorkoutStep.Rest -> {
                speechManager.say("Repos, ${step.duration.totalSeconds()} secondes") {
                    startTimer(step.duration)
                }
            }

            is WorkoutStep.Timed -> {
                speechManager.say(
                    "${step.title}, ${step.duration.totalSeconds()} secondes. Go!"
                ) {
                    startTimer(step.duration)
                }
            }

            is WorkoutStep.Manual -> {
                speechManager.say(step.title)
                state = state.copy(
                    isRunning = false
                )
            }

            null -> {
                state = state.copy(
                    isFinished = true,
                    isRunning = false,
                    isPaused = false
                )
            }
        }
    }

    fun completeCurrentStep() {
        moveToNextStep()
    }

    fun pause() {
        if (!state.isRunning)
            return

        timerJob?.cancel()

        state = state.copy(
            isRunning = false,
            isPaused = true
        )
    }

    fun resume() {
        if (!state.isPaused)
            return
        speechManager.say("Reprise. Go!") {
            startTimer(Duration(), true)
        }
    }

    private fun moveToNextStep() {
        val nextIndex = state.currentStepIndex + 1

        if (nextIndex >= (state.workout?.steps?.size ?: 0)) {
            speechManager.say("Séance terminée")
            state = state.copy(
                isFinished = true,
                isRunning = false,
                isPaused = false
            )
            return
        }

        speechManager.say("Suivant") {
            state = state.copy(
                currentStepIndex = nextIndex
            )
            startCurrentStep()
        }
    }

    private fun startTimer(duration: Duration, resuming: Boolean = false) {

        if (!resuming)
            timerJob?.cancel()

        val remainingSeconds = if (resuming) state.remainingSeconds else duration.totalSeconds()
        state = state.copy(
            remainingSeconds = remainingSeconds,
            isRunning = true,
            isPaused = false
        )

        timerJob = viewModelScope.launch {
            while (state.remainingSeconds > 0) {
                delay(1000)

                val newValue = state.remainingSeconds - 1

                state = state.copy(remainingSeconds = newValue)

                if (newValue in 1..5) {
                    speechManager.say(newValue.toString())
                }
            }

            moveToNextStep()
        }
    }
}