package com.claudemr.tabata.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object WorkoutEditor : Screen("workoutEditor/{workoutId}") {
        fun route(workoutId: Long) =
            "workoutEditor/$workoutId"
    }
    data object StepEditor : Screen("stepEditor/{stepIndex}") {
        fun route(stepIndex: Int): String =
            "stepEditor/$stepIndex"
    }

    object Runner : Screen("runner/{workoutId}") {
        fun route(workoutId: Long) =
            "runner/$workoutId"
    }
    data object Settings : Screen("settings")
}