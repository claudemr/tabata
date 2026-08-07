package com.claudemr.tabata.model

import kotlinx.serialization.Serializable

@Serializable
sealed class WorkoutStep {
    abstract val name: String
    abstract val description: String
    abstract val rest: Duration
    abstract val nbSets: Int
    abstract fun subtitle(): String

    @Serializable
    data class Manual(
        override val name: String,
        override val description: String,
        override val rest: Duration,
        override val nbSets: Int,
        val nbReps: Int
    ) : WorkoutStep() {
        override fun subtitle() = "${nbReps} répétitions"
    }

    @Serializable
    data class Timed(
        override val name: String,
        override val description: String,
        override val rest: Duration,
        override val nbSets: Int,
        val duration: Duration
    ) : WorkoutStep() {
        override fun subtitle() = duration.toString()
    }

}