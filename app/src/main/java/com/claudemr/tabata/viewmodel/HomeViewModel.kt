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
        return  workoutRepository.findById(workoutId)
    }

    fun delete(workoutId: Long) {
        val workout = workoutRepository.findById(workoutId)
        Log.w("TABATA", "MIAOU delete ${workout?.name}")

        //workoutRepository.delete(workoutId)
    }
}