package com.claudemr.tabata.viewmodel

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.claudemr.tabata.TabataApplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.claudemr.tabata.AppContainer


@Composable
inline fun <reified T : ViewModel> appViewModel(): T {

    val app =
        LocalContext.current.applicationContext
                as TabataApplication

    return viewModel(
        factory = ViewModelFactory(
            app.container
        )
    )
}

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(
                    container.workoutRepository
                ) as T

            modelClass.isAssignableFrom(WorkoutEditorViewModel::class.java) ->
                WorkoutEditorViewModel(
                    container.workoutRepository
                ) as T

            modelClass.isAssignableFrom(StepEditorViewModel::class.java) ->
                StepEditorViewModel() as T

            modelClass.isAssignableFrom(RunnerViewModel::class.java) ->
                RunnerViewModel(
                    container.workoutRepository,
                    container.speechManager
                ) as T

            else ->
                throw IllegalArgumentException(
                    "Unknown ViewModel class: ${modelClass.name}"
                )
        }
    }
}