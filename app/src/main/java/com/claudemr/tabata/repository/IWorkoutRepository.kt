package com.claudemr.tabata.repository

import com.claudemr.tabata.model.Workout
import kotlinx.coroutines.flow.StateFlow

sealed class ImportResult {
    data object Success : ImportResult()
    data class Error(val message: String) : ImportResult()
}

interface IWorkoutRepository {
    val workouts: StateFlow<List<Workout>>
    fun save(workout: Workout): Workout
    fun findById(id: Long): Workout?
    fun delete(id: Long)
    fun copy(workout: Workout): Workout

    fun exportJson(): String
    fun importJson(json: String) : ImportResult
}
