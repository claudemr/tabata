package com.claudemr.tabata.repository

import com.claudemr.tabata.model.Workout
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WorkoutDatabase(
    @Transient
    val nextId: Long = 1,
    val workouts: List<Workout> = emptyList()
)