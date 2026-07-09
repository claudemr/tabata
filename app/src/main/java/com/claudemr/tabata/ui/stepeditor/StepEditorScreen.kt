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
    val viewModel: StepEditorViewModel = viewModel()
    val step = if (isEditing) workoutEditorViewModel.state.steps[stepIndex] else null
    val initialDuration = when (step) {
        is WorkoutStep.Rest -> step.duration.totalSeconds()
        is WorkoutStep.Timed -> step.duration.totalSeconds()
        else -> 30
    }

    var minutes by remember(stepIndex) {
        mutableStateOf((initialDuration / 60).toString())
    }

    var seconds by remember(stepIndex) {
        mutableStateOf((initialDuration % 60).toString())
    }


    LaunchedEffect(stepIndex) {
        if (isEditing) {
            viewModel.load(workoutEditorViewModel.getStep(stepIndex))
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
                    selected = viewModel.state.type == type,
                    onClick = { viewModel.setType(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = StepType.entries.size
                    )
                ) {
                    Text(
                        when (type) {
                            StepType.MANUAL -> "Manuel"
                            StepType.TIMED -> "Timer"
                            StepType.REST -> "Repos"
                        }
                    )
                }
            }
        }

        if (viewModel.state.type != StepType.REST) {
            OutlinedTextField(
                value = viewModel.state.title,
                onValueChange = viewModel::setTitle,
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.state.description,
                onValueChange = viewModel::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            viewModel.state.type == StepType.TIMED ||
            viewModel.state.type == StepType.REST
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minutes,
                    onValueChange = {
                        minutes = it

                        viewModel.setDuration(
                            Duration(
                                minutes = minutes.toIntOrNull() ?: 0,
                                seconds = seconds.toIntOrNull() ?: 0
                            )
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

                        viewModel.setDuration(
                            Duration(
                                minutes = minutes.toIntOrNull() ?: 0,
                                seconds = seconds.toIntOrNull() ?: 0
                            )
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val step = viewModel.buildStep()

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