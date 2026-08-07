package com.claudemr.tabata.ui.stepeditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.claudemr.tabata.model.Duration
import com.claudemr.tabata.model.WorkoutStep
import com.claudemr.tabata.state.StepType
import com.claudemr.tabata.viewmodel.StepEditorViewModel
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel

@Composable
fun StepEditorScreen(
    navController: NavHostController,
    workoutEditorViewModel: WorkoutEditorViewModel,
    stepIndex: Int
) {
    val isEditing = stepIndex >= 0
    val stepEditorViewModel: StepEditorViewModel = viewModel()
    val step = if (isEditing) workoutEditorViewModel.state.steps[stepIndex] else null
    val initialDuration = when (step) {
        is WorkoutStep.Timed -> step.duration.seconds
        else -> 60
    }
    val initialNbReps = when (step) {
        is WorkoutStep.Manual -> step.nbReps
        else -> 15
    }
    val initialRestDuration = when (step) {
        is WorkoutStep.Timed, is WorkoutStep.Manual -> step.rest.seconds
        else -> 60
    }
    val initialNbSets = when (step) {
        is WorkoutStep.Timed, is WorkoutStep.Manual -> step.nbSets
        else -> 1
    }

    var minutes by remember(stepIndex) {
        mutableStateOf((initialDuration / 60).toString())
    }

    var seconds by remember(stepIndex) {
        mutableStateOf((initialDuration % 60).toString())
    }

    var restMins by remember(stepIndex) {
        mutableStateOf((initialRestDuration / 60).toString())
    }

    var restSecs by remember(stepIndex) {
        mutableStateOf((initialRestDuration % 60).toString())
    }

    var nbReps by remember(stepIndex) {
        mutableStateOf(initialNbReps.toString())
    }

    var nbSets by remember(stepIndex) {
        mutableStateOf(initialNbSets.toString())
    }


    LaunchedEffect(stepIndex) {
        if (isEditing) {
            stepEditorViewModel.load(workoutEditorViewModel.getStep(stepIndex))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Ajouter une étape",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Type")

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            StepType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = stepEditorViewModel.state.type == type,
                    onClick = { stepEditorViewModel.setType(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = StepType.entries.size
                    )
                ) {
                    Text(
                        when (type) {
                            StepType.MANUAL -> "Manuel"
                            StepType.TIMED -> "Timer"
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = stepEditorViewModel.state.name,
            onValueChange = stepEditorViewModel::setTitle,
            label = { Text("Nom") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = stepEditorViewModel.state.description,
            onValueChange = stepEditorViewModel::setDescription,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        when (stepEditorViewModel.state.type) {
            StepType.MANUAL -> {
                OutlinedTextField(
                    value = nbReps,
                    onValueChange = {
                        nbReps = it
                        stepEditorViewModel.setNbReps(nbReps.toIntOrNull() ?: 0)
                    },
                    label = { Text("Répétitions") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }

            StepType.TIMED -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = {
                            minutes = it
                            val curMin = minutes.toIntOrNull() ?: 0
                            val curSec = seconds.toIntOrNull() ?: 0
                            stepEditorViewModel.setDuration(
                                Duration(curMin * 60 + curSec)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    OutlinedTextField(
                        value = seconds,
                        onValueChange = {
                            seconds = it
                            val curMin = minutes.toIntOrNull() ?: 0
                            val curSec = seconds.toIntOrNull() ?: 0
                            stepEditorViewModel.setDuration(
                                Duration(curMin * 60 + curSec)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Secondes") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Repos")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = restMins,
                onValueChange = {
                    restMins = it
                    val curMin = restMins.toIntOrNull() ?: 0
                    val curSec = restSecs.toIntOrNull() ?: 0

                    stepEditorViewModel.setRestDuration(
                        Duration(curMin * 60 + curSec)
                    )
                },
                modifier = Modifier.weight(1f),
                label = { Text("Minutes") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = restSecs,
                onValueChange = {
                    restSecs = it
                    val curMin = restMins.toIntOrNull() ?: 0
                    val curSec = restSecs.toIntOrNull() ?: 0

                    stepEditorViewModel.setRestDuration(
                        Duration(curMin * 60 + curSec)
                    )
                },
                modifier = Modifier.weight(1f),
                label = { Text("Secondes") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nombre de série")

        OutlinedTextField(
            value = nbSets,
            onValueChange = {
                nbSets = it
                stepEditorViewModel.setNbSets(nbSets.toIntOrNull() ?: 0)
            },
            label = { Text("Nombre de série") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val step = stepEditorViewModel.buildStep()

                if (stepIndex == -1)
                    workoutEditorViewModel.addStep(step)
                else
                    workoutEditorViewModel.updateStep(
                        stepIndex,
                        step
                    )

                navController.popBackStack()
            }
        ) {
            Text("Valider")
        }
    }
}