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
        state = state.copy(title = title)
    }

    fun setDescription(description: String) {
        state = state.copy(description = description)
    }

    fun setDuration(duration: Duration) {
        state = state.copy(duration = duration)
    }

    fun buildStep(): WorkoutStep {
        return when (state.type) {
            StepType.MANUAL ->
                WorkoutStep.Manual(
                    title = state.title,
                    description = state.description
                )

            StepType.TIMED ->
                WorkoutStep.Timed(
                    title = state.title,
                    description = state.description,
                    duration = state.duration
                )

            StepType.REST ->
                WorkoutStep.Rest(
                    duration = state.duration
                )
        }
    }

    fun load(step: WorkoutStep) {
        when(step) {
            is WorkoutStep.Manual ->
                state = StepEditorState(
                    type = StepType.MANUAL,
                    title = step.title,
                    description = step.description
                )

            is WorkoutStep.Timed ->
                state = StepEditorState(
                    type = StepType.TIMED,
                    title = step.title,
                    description = step.description,
                    duration = step.duration
                )

            is WorkoutStep.Rest ->
                state = StepEditorState(
                    type = StepType.REST,
                    duration = step.duration
                )
        }
    }
}