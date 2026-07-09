package com.claudemr.tabata.model

import kotlinx.serialization.Serializable

@Serializable
sealed class WorkoutStep {
    abstract val title: String
    abstract val description: String
    abstract fun subtitle(): String

    @Serializable
    data class Manual(
        override val title: String,
        override val description: String
    ) : WorkoutStep() {
        override fun subtitle() = "Exercice manuel"
    }

    @Serializable
    data class Timed(
        override val title: String,
        override val description: String,
        val duration: Duration
    ) : WorkoutStep() {
        override fun subtitle() = duration.toString()
    }

    @Serializable
    data class Rest(
        val duration: Duration
    ) : WorkoutStep() {
        override val title = "Repos"
        override val description = ""
        override fun subtitle() = duration.toString()
    }
}