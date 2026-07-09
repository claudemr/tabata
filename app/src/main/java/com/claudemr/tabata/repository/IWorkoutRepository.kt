package com.claudemr.tabata.repository

import com.claudemr.tabata.model.Workout
import kotlinx.coroutines.flow.StateFlow

interface IWorkoutRepository {
    val workouts: StateFlow<List<Workout>>
    fun save(workout: Workout): Workout
    fun findById(id: Long): Workout?
    fun delete(id: Long)
}
