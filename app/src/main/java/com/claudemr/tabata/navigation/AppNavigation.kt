package com.claudemr.tabata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.claudemr.tabata.ui.home.HomeScreen
import com.claudemr.tabata.ui.runner.RunnerScreen
import com.claudemr.tabata.ui.stepeditor.StepEditorScreen
import com.claudemr.tabata.ui.workouteditor.WorkoutEditorScreen
import com.claudemr.tabata.viewmodel.appViewModel
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val workoutEditorViewModel: WorkoutEditorViewModel = appViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                workoutEditorViewModel = workoutEditorViewModel
            )
        }

        composable(
            route = Screen.WorkoutEditor.route,
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workoutId =
                backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutEditorScreen(
                navController = navController,
                workoutEditorViewModel = workoutEditorViewModel,
                workoutId = workoutId
            )
        }

        composable(
            route = Screen.StepEditor.route,
            arguments = listOf(
                navArgument("stepIndex") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val stepIndex = backStackEntry.arguments?.getInt("stepIndex") ?: -1
            StepEditorScreen(
                navController = navController,
                workoutEditorViewModel = workoutEditorViewModel,
                stepIndex = stepIndex
            )
        }

        composable(
            route = Screen.Runner.route,
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workoutId =
                backStackEntry.arguments?.getLong("workoutId") ?: 0L
            RunnerScreen(
                navController = navController,
                workoutId = workoutId
            )
        }

        composable(Screen.Settings.route) {
        }

    }
}