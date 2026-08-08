package com.claudemr.tabata.ui.workouteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel
import com.claudemr.tabata.navigation.Screen

@Composable
fun WorkoutEditorScreen(
    navController: NavHostController,
    workoutEditorViewModel: WorkoutEditorViewModel,
    workoutId: Long
) {
    var showDeleteDialog by remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(workoutId) {
        workoutEditorViewModel.load(workoutId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Circuit",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = workoutEditorViewModel.state.name,
            onValueChange = workoutEditorViewModel::setName,
            label = { Text("Nom") }
        )

        OutlinedTextField(
            value = workoutEditorViewModel.state.description,
            onValueChange = workoutEditorViewModel::setDescription,
            label = { Text("Description") }
        )

        OutlinedTextField(
            value = workoutEditorViewModel.state.nbTurns.toString(),
            onValueChange = workoutEditorViewModel::setNbTurns,
            label = { Text("Nombre de tours") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

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

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    Screen.StepEditor.route(-1)
                )
            }
        ) {
            Text("Ajouter un exercice")
        }

        Spacer(Modifier.height(8.dp))

        if (workoutEditorViewModel.state.steps.isEmpty()) {
            Text("Aucun exercice")
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
                            Text(step.name)
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
                                    workoutEditorViewModel.copyStep(index)
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, null)
                            }

                            IconButton(
                                onClick = {
                                    showDeleteDialog = index
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

    // Handle confirmation of delete
    if (showDeleteDialog >= 0) {
        val stepId = showDeleteDialog
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = -1
            },
            title = {
                Text("Supprimer un exercice ?")
            },
            text = {
                Text("Voulez-vous vraiment supprimer '${workoutEditorViewModel.getStep(stepId).name}'")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        workoutEditorViewModel.removeStep(stepId)
                        showDeleteDialog = -1
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = -1
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}
