package com.claudemr.tabata.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.claudemr.tabata.model.Workout
import com.claudemr.tabata.model.WorkoutStep
import com.claudemr.tabata.repository.IWorkoutRepository
import com.claudemr.tabata.state.WorkoutEditorState

class WorkoutEditorViewModel(
    private val workoutRepository: IWorkoutRepository
) : ViewModel() {
    var state by mutableStateOf(WorkoutEditorState())
        private set

    private val repository = workoutRepository

    fun reset() {
        state = WorkoutEditorState()
    }

    fun setName(name: String) {
        state = state.copy(name = name)
    }

    fun setDescription(description: String) {
        state = state.copy(description = description)
    }

    fun setNbTurns(nbTurns: String) {
        state = state.copy(nbTurns = nbTurns.toIntOrNull() ?: 0)
    }

    fun save() {
        val savedWorkout = repository.save(
            Workout(
                id = state.id,
                name = state.name,
                description = state.description,
                nbTurns = state.nbTurns,
                steps = state.steps.toMutableList()
            )
        )

        // Get the generated id
        state = state.copy(id = savedWorkout.id)
    }

    fun load(workoutId: Long) {
        if (workoutId == 0L)
            return

        // If the viewmodel already contains the right workout id,
        // no need to load it and stomp the state
        if (state.id == workoutId) {
            return
        }

        val workout = repository.findById(workoutId)
            ?: return

        state = WorkoutEditorState(
            id = workout.id,
            name = workout.name,
            description = workout.description,
            nbTurns = workout.nbTurns,
            steps = workout.steps
        )
    }

    fun addStep(step: WorkoutStep) {
        Log.d("TABATA", "Add step_title=${step.name}")
        state = state.copy(
            steps = state.steps + step
        )
    }

    fun removeStep(index: Int) {
        state = state.copy(
            steps = state.steps.filterIndexed { i, _ -> i != index }
        )
    }

    fun updateStep(index: Int, step: WorkoutStep) {
        state = state.copy(
            steps = state.steps.mapIndexed { i, current ->
                if (i == index) step else current
            }
        )
    }

    fun getStep(index: Int): WorkoutStep =
        state.steps[index]

    fun moveStepUp(index: Int) {
        if (index == 0)
            return

        val list = state.steps.toMutableList()

        list[index] =
            list[index - 1]
                .also {
                    list[index - 1] = list[index]
                }

        state = state.copy(steps = list)
    }

    fun moveStepDown(index: Int) {
        if (index == state.steps.lastIndex)
            return

        val list = state.steps.toMutableList()

        list[index] =
            list[index + 1]
                .also {
                    list[index + 1] = list[index]
                }

        state = state.copy(steps = list)
    }
}
