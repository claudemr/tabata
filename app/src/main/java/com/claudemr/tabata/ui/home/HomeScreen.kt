package com.claudemr.tabata.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.claudemr.tabata.navigation.Screen
import com.claudemr.tabata.viewmodel.HomeViewModel
import com.claudemr.tabata.viewmodel.WorkoutEditorViewModel
import com.claudemr.tabata.viewmodel.appViewModel
import com.claudemr.tabata.repository.ImportResult


@Composable
fun HomeScreen(
    navController: NavHostController,
    workoutEditorViewModel: WorkoutEditorViewModel,
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = appViewModel()
    val workouts by homeViewModel.workouts.collectAsState()

    var showDeleteDialog by remember {
        mutableLongStateOf(-1)
    }

    var importError by remember {
        mutableStateOf<String?>(null)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) {  uri ->

        uri ?: return@rememberLauncherForActivityResult

        val json = homeViewModel.exportJson()

        context.contentResolver
            .openOutputStream(uri)
            ?.bufferedWriter(Charsets.UTF_8)
            ?.use { writer ->
                writer.write(json)
            }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        uri ?: return@rememberLauncherForActivityResult

        try {
            val json = context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { reader -> reader.readText() }

            if (json != null) {
                when (val result = homeViewModel.importJson(json)) {
                    ImportResult.Success -> {
                        importError = null
                    }

                    is ImportResult.Error -> {
                        importError = result.message
                    }
                }
            } else {
                importError = "Impossible de lire le fichier."
            }
        } catch (e: Exception) {
            importError = "Impossible de lire le fichier."
        }
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

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                importLauncher.launch(arrayOf("application/json"))
            }
        ) {
            Text("Importer circuits")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                exportLauncher.launch("tabata-workouts.json")
            }
        ) {
            Text("Exporter circuits")
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
                                        homeViewModel.copy(workout)
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, null)
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

    if (importError != null) {
        AlertDialog(
            onDismissRequest = {
                importError = null
            },
            title = {
                Text("Import impossible")
            },
            text = {
                Text(importError!!)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        importError = null
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}