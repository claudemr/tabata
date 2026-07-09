package com.claudemr.tabata.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import com.claudemr.tabata.repository.IWorkoutRepository
import com.claudemr.tabata.model.Workout

class HomeViewModel(
    private val workoutRepository: IWorkoutRepository
) : ViewModel() {
    val workouts: StateFlow<List<Workout>> =
        workoutRepository.workouts

    fun delete(workout: Workout) {
        workoutRepository.delete(workout.id)
    }
}