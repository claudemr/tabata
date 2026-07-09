package com.claudemr.tabata.repository

import com.claudemr.tabata.model.Workout
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDatabase(
    val nextId: Long = 1,
    val workouts: List<Workout> = emptyList()
)