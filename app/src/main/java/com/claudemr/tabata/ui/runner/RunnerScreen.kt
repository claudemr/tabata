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
            text = "Circuit terminé !",
            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = state.workout?.name ?: "",
            style = MaterialTheme.typography.headlineLarge
        )

        if ((state.workout?.nbTurns ?: 0) > 1)
            Text("Tour ${state.currentTurn + 1} / ${state.workout?.nbTurns}")

        when (step) {
            is WorkoutStep.Timed -> {
                Text(
                    text = step.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (step.description.isNotBlank())
                    Text(step.description)
                if (step.nbSets > 1)
                    Text(
                        text = "Série ${state.currentSet + 1} / ${step.nbSets}",
                        style = MaterialTheme.typography.headlineSmall
                    )
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
                        Text(
                            text = "EN PAUSE",
                            style = MaterialTheme.typography.headlineSmall
                        )

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
                Text(
                    text = step.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (step.description.isNotBlank())
                    Text(step.description)
                if (step.nbSets > 1)
                    Text(
                        text = "Série ${state.currentSet + 1} / ${step.nbSets}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                Text("Répétitions: ${step.nbReps}")
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
                Text("Circuit terminé")
            }
        }

        if (state.isResting) {
            Text(
                text = "Repos",
                style = MaterialTheme.typography.headlineSmall
            )
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
                Text(
                    text = "EN PAUSE",
                    style = MaterialTheme.typography.headlineSmall
                )

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