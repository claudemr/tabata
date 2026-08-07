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
            is WorkoutStep.Timed -> {
                Text(step.name)
                Text(step.description)
                Text("Série ${state.currentSet + 1} / ${step.nbSets}")
                if (!state.isResting) {
                    Text("${state.remainingSeconds}s")
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

                    Button(
                        onClick = {
                            runnerViewModel.moveForward()
                        }
                    ) {
                        Text("Suivant")
                    }
                }
            }

            is WorkoutStep.Manual -> {
                Text(step.name)
                Text(step.description)
                Text("Répétitions: ${step.nbReps}")
                Text("Série ${state.currentSet + 1} / ${step.nbSets}")
                if (!state.isResting) {
                    Button(
                        onClick = {
                            runnerViewModel.moveForward()
                        }
                    ) {
                        Text("Série terminée")
                    }
                }
            }

            null -> {
                Text("Séance terminée")
            }
        }

        if (state.isResting) {
            Text("Repos")
            Text("${state.remainingSeconds}s")
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

            Button(
                onClick = {
                    runnerViewModel.moveForward()
                }
            ) {
                Text("Suivant")
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "Exercice ${state.currentStepIndex + 1} / ${state.workout?.steps?.size ?: 0}"
        )
    }
}