package com.claudemr.tabata.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import com.claudemr.tabata.repository.IWorkoutRepository
import com.claudemr.tabata.model.Workout

import android.util.Log

class HomeViewModel(
    private val workoutRepository: IWorkoutRepository
) : ViewModel() {
    val workouts: StateFlow<List<Workout>> =
        workoutRepository.workouts

    fun findById(workoutId: Long) : Workout? {
        return workoutRepository.findById(workoutId)
    }

    fun copy(workout: Workout) {
        workoutRepository.copy(workout)
    }

    fun delete(workoutId: Long) {
        workoutRepository.delete(workoutId)
    }
}