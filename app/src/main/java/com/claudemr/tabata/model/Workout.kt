package com.claudemr.tabata.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Workout(
    @Transient
    val id: Long = 0,
    val name: String,
    val description: String,
    val nbTurns: Int,
    val steps: MutableList<WorkoutStep> = mutableListOf()
)