package com.claudemr.tabata.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: Long = 0,
    val name: String,
    val description: String,
    val steps: MutableList<WorkoutStep> = mutableListOf()
)