package com.claudemr.tabata.repository

import com.claudemr.tabata.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InMemoryWorkoutRepository : IWorkoutRepository {
    private var nextId = 1L
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())

    override val workouts: StateFlow<List<Workout>>
        get() = _workouts

    override fun save(workout: Workout): Workout {
        val list = _workouts.value.toMutableList()

        // Create new workout in the list
        if (workout.id == 0L) {
            val savedWorkout = workout.copy(id = nextId++)
            list.add(savedWorkout)
            _workouts.value = list
            return savedWorkout
        }

        val index = list.indexOfFirst {
            it.id == workout.id
        }

        if (index != -1) {
            list[index] = workout
            _workouts.value = list
        }

        return workout
    }

    override fun findById(id: Long): Workout? {
        return _workouts.value.firstOrNull {
            it.id == id
        }
    }

    override fun delete(id: Long) {
        _workouts.value =
            _workouts.value.filterNot { it.id == id }
    }
}