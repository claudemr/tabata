package com.claudemr.tabata.ui.runner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.claudemr.tabata.model.WorkoutStep
import com.claudemr.tabata.viewmodel.RunnerViewModel
import com.claudemr.tabata.viewmodel.appViewModel

@Composable
fun RunnerScreen(
    navController: NavHostController,
    workoutId: Long
) {
    val runnerViewModel: RunnerViewModel = appViewModel()
    val state = runnerViewModel.state
    val step = state.currentStep

    LaunchedEffect(workoutId) {
        runnerViewModel.initialize(workoutId)
    }

    if (!state.isTtsReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Initialisation des annonces vocales...")
        }
        return
    }

    if (state.isFinished) {
        Text(
            text = "Séance terminée !"
        )
        return
    }

    Column {

        Text(
            text = state.workout?.name ?: "",
            style = MaterialTheme.typography.headlineSmall
        )

        when (step) {
            is WorkoutStep.Rest -> {
                Text("Repos")
                Text("${state.remainingSeconds}s")
            }

            is WorkoutStep.Timed -> {
                Text(step.title)
                Text(step.description)
                Text("${state.remainingSeconds}s")
            }

            is WorkoutStep.Manual -> {
                Text(step.title)
                Text(step.description)

                Button(
                    onClick = {
                        runnerViewModel.completeCurrentStep()
                    }
                ) {
                    Text("Exercice terminé")
                }
            }

            null -> {
                Text("Séance terminée")
            }
        }
        if (step is WorkoutStep.Rest || step is WorkoutStep.Timed) {
            Button(
                onClick = {
                    if (state.isPaused)
                        runnerViewModel.resume()
                    else
                        runnerViewModel.pause()
                }
            ) {
                Text(
                    if (state.isPaused)
                        "Reprendre"
                    else
                        "Pause"
                )
            }
            if (state.isPaused)
                Text("PAUSE")
        }
        Spacer(Modifier.height(32.dp))

        Text(
            "Exercice ${state.currentStepIndex + 1} / ${state.workout?.steps?.size ?: 0}"
        )
    }
}