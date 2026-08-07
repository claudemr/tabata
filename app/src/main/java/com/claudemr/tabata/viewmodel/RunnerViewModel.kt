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
            startCurrentTurn()
        }
    }

    fun moveForward() {
        timerJob?.cancel()

        when (val step = state.currentStep) {
            null -> {
                state = state.copy(
                    isFinished = true,
                    isRunning = false,
                    isPaused = false
                )
            }
            else -> {
                if (!state.isResting) {
                    state = state.copy(
                        isResting = true,
                        remainingSeconds = step.rest.seconds
                    )
                    speechManager.say(
                        "Repos, ${step.rest.toFrenchTts()}. C'est parti!"
                    ) {
                        startTimer(step.rest)
                    }
                } else {
                    val nextSet = state.currentSet + 1
                    if (nextSet < step.nbSets) {
                        state = state.copy(
                            isResting = false,
                            currentSet = nextSet
                        )
                        startCurrentStep()
                    } else {
                        state = state.copy(
                            isResting = false
                        )
                        moveToNextStep()
                    }
                }
            }
        }
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

    private fun startCurrentTurn() {
        val turnNumber = state.currentTurn + 1

        speechManager.say(
            "Circuit ${state.workout?.name}. " +
                    "Tour ${turnNumber} sur ${state.workout?.nbTurns}."
        ) {
            startCurrentStep()
        }
    }

    private fun startCurrentStep() {
        val setNumber = state.currentSet + 1

        when (val step = state.currentStep) {
            is WorkoutStep.Timed -> {
                state = state.copy(
                    remainingSeconds = step.duration.seconds
                )

                speechManager.say(
                    "${step.name}. Série ${setNumber} sur ${step.nbSets}. " +
                            "Durée : ${step.duration.toFrenchTts()}. " +
                            "Préparez-vous : 3, 2, 1, go!"
                ) {
                    startTimer(step.duration)
                }
            }

            is WorkoutStep.Manual -> {
                speechManager.say(
                    "${step.name}. Série ${setNumber} sur ${step.nbSets}. " +
                            "${step.nbReps} répétitions."
                )
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

    private fun moveToNextTurn() {
        val nextTurn = state.currentTurn + 1

        if (nextTurn >= (state.workout?.nbTurns ?: 0)) {
            speechManager.say("Circuit terminé")
            state = state.copy(
                isFinished = true,
                isRunning = false,
                isPaused = false
            )
            return
        }

        speechManager.say("Tour suivant") {
            state = state.copy(
                currentTurn = nextTurn,
                currentStepIndex = 0,
                currentSet = 0
            )
            startCurrentTurn()
        }
    }

    private fun moveToNextStep() {
        val nextIndex = state.currentStepIndex + 1

        if (nextIndex >= (state.workout?.steps?.size ?: 0)) {
            moveToNextTurn()
            return
        }

        speechManager.say("Exercice suivant") {
            state = state.copy(
                currentStepIndex = nextIndex,
                currentSet = 0
            )
            startCurrentStep()
        }
    }

    private fun startTimer(duration: Duration, resuming: Boolean = false) {

        if (!resuming)
            timerJob?.cancel()

        val remainingSeconds = if (resuming) state.remainingSeconds else duration.seconds
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

            moveForward()
        }
    }
}