package com.claudemr.tabata.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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
    val viewModel: HomeViewModel = appViewModel()
    val workouts by viewModel.workouts.collectAsState()

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

                            Text(workout.description)

                            Row {

                                Button(
                                    onClick = {
                                        navController.navigate(
                                            Screen.Runner.route(workout.id)
                                        )
                                    }
                                ) {
                                    Text("Lancer")
                                }

                                Button(
                                    onClick = {
                                        workoutEditorViewModel.reset()
                                        navController.navigate(
                                            Screen.WorkoutEditor.route(workout.id)
                                        )
                                    }
                                ) {
                                    Text("Modifier")
                                }

                                Button(
                                    onClick = {
                                        viewModel.delete(workout)
                                    }
                                ) {
                                    Text("Supprimer")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                workoutEditorViewModel.reset()
                navController.navigate(Screen.WorkoutEditor.route(0))
            }
        ) {
            Text("Nouvelle séance")
        }
    }
}