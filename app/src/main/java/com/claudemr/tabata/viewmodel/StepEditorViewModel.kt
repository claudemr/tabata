package com.claudemr.tabata.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.claudemr.tabata.model.Duration
import com.claudemr.tabata.model.WorkoutStep
import com.claudemr.tabata.state.StepType
import com.claudemr.tabata.state.StepEditorState

class StepEditorViewModel : ViewModel() {

    var state by mutableStateOf(StepEditorState())
        private set

    fun setType(type: StepType) {
        state = state.copy(type = type)
    }

    fun setTitle(title: String) {
        state = state.copy(name = title)
    }

    fun setDescription(description: String) {
        state = state.copy(description = description)
    }

    fun setNbReps(nbReps: Int) {
        state = state.copy(nbReps = nbReps)
    }

    fun setDuration(duration: Duration) {
        state = state.copy(duration = duration)
    }

    fun setRestDuration(rest: Duration) {
        state = state.copy(rest = rest)
    }

    fun setNbSets(nbSets: Int) {
        state = state.copy(nbSets = nbSets)
    }

    fun buildStep(): WorkoutStep {
        return when (state.type) {
            StepType.MANUAL ->
                WorkoutStep.Manual(
                    name = state.name,
                    description = state.description,
                    nbReps = state.nbReps,
                    rest = state.rest,
                    nbSets = state.nbSets
                )

            StepType.TIMED ->
                WorkoutStep.Timed(
                    name = state.name,
                    description = state.description,
                    duration = state.duration,
                    rest = state.rest,
                    nbSets = state.nbSets
                )
        }
    }

    fun load(step: WorkoutStep) {
        when(step) {
            is WorkoutStep.Manual ->
                state = StepEditorState(
                    type = StepType.MANUAL,
                    name = step.name,
                    description = step.description,
                    nbReps = step.nbReps,
                    rest = step.rest,
                    nbSets = step.nbSets
                )

            is WorkoutStep.Timed ->
                state = StepEditorState(
                    type = StepType.TIMED,
                    name = step.name,
                    description = step.description,
                    duration = step.duration,
                    rest = step.rest,
                    nbSets = step.nbSets
                )
        }
    }
}