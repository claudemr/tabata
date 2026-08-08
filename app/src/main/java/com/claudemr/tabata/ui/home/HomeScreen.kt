package com.claudemr.tabata.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.claudemr.tabata.navigation.Screen
import com.claudemr.tabata.viewmodel.HomeViewModel
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel
import com.claudemr.tabata.viewmodel.appViewModel



@Composable
fun HomeScreen(
    navController: NavHostController,
    workoutEditorViewModel: WorkoutEditorViewModel,
) {
    val homeViewModel: HomeViewModel = appViewModel()
    val workouts by homeViewModel.workouts.collectAsState()

    var showDeleteDialog by remember {
        mutableLongStateOf(-1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "TABATA",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                workoutEditorViewModel.reset()
                navController.navigate(Screen.WorkoutEditor.route(0))
            }
        ) {
            Text("Nouveau circuit")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mes circuits",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (workouts.isEmpty()) {
            Text("Aucun circuit enregistré.")
        } else {
            LazyColumn {
                items(workouts) { workout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                workout.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (workout.description.isNotBlank())
                                Text(workout.description)

                            Row {
                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            Screen.Runner.route(workout.id)
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.PlayArrow, null)
                                }

                                IconButton(
                                    onClick = {
                                        workoutEditorViewModel.reset()
                                        navController.navigate(
                                            Screen.WorkoutEditor.route(workout.id)
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, null)
                                }

                                IconButton(
                                    onClick = {
                                        showDeleteDialog = workout.id
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Handle confirmation of delete
    if (showDeleteDialog >= 0) {
        val workoutId = showDeleteDialog
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = -1
            },
            title = {
                Text("Supprimer le circuit ?")
            },
            text = {
                Text("Voulez-vous vraiment supprimer '${homeViewModel.findById(workoutId)?.name}'")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        homeViewModel.delete(workoutId)
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