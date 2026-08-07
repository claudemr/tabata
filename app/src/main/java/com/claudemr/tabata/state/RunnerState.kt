package com.claudemr.tabata.state

import com.claudemr.tabata.model.Workout
import com.claudemr.tabata.model.WorkoutStep

data class RunnerState(
    val workout: Workout? = null,
    val currentTurn: Int = 0,
    val currentStepIndex: Int = 0,
    val remainingSeconds: Int = 0,
    val currentSet: Int = 0,
    val isTtsReady: Boolean = false,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isResting: Boolean = false,
    val isFinished: Boolean = false
) {
    val currentStep: WorkoutStep?
        get() = workout?.steps?.getOrNull(currentStepIndex)
}
