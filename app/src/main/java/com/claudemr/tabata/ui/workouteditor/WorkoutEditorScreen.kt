package com.claudemr.tabata.ui.workouteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel
import com.claudemr.tabata.navigation.Screen

import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun WorkoutEditorScreen(
    navController: NavHostController,
    workoutEditorViewModel: WorkoutEditorViewModel,
    workoutId: Long
) {
    LaunchedEffect(workoutId) {
        Log.d("TABATA", "workoutId=$workoutId")
        workoutEditorViewModel.load(workoutId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Nouvelle séance",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = workoutEditorViewModel.state.name,
            onValueChange = workoutEditorViewModel::updateName,
            label = { Text("Nom") }
        )

        OutlinedTextField(
            value = workoutEditorViewModel.state.description,
            onValueChange = workoutEditorViewModel::updateDescription,
            label = { Text("Description") }
        )

        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.weight(1f))

        Text(
            text = "Étapes",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    Screen.StepEditor.route(-1)
                )
            }
        ) {
            Text("Ajouter une étape")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = workoutEditorViewModel.state.name.isNotBlank(),
            onClick = {
                workoutEditorViewModel.save()
                navController.popBackStack()
            }
        ) {
            Text("Enregistrer")
        }

        Spacer(Modifier.height(8.dp))

        if (workoutEditorViewModel.state.steps.isEmpty()) {
            Text("Aucune étape")
        } else {
            LazyColumn {
                itemsIndexed(workoutEditorViewModel.state.steps) { index, step ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = {
                            navController.navigate(
                                Screen.StepEditor.route(index)
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(step.title)
                            Text(step.subtitle())
                        }
                        Row {
                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.StepEditor.route(index)
                                    )
                                }
                            ) {
                                Icon(Icons.Default.Edit, null)
                            }

                            IconButton(
                                onClick = {
                                    workoutEditorViewModel.removeStep(index)
                                }
                            ) {
                                Icon(Icons.Default.Delete, null)
                            }

                            IconButton(
                                onClick = {
                                    workoutEditorViewModel.moveStepUp(index)
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, null)
                            }

                            IconButton(
                                onClick = {
                                    workoutEditorViewModel.moveStepDown(index)
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, null)
                            }
                        }
                    }
                }
            }
        }
    }
}
