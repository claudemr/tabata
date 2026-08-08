package com.claudemr.tabata.repository

import android.content.Context
import com.claudemr.tabata.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.io.File


class JsonWorkoutRepository(
    private val context: Context
) : IWorkoutRepository {

    private val file = File(context.filesDir, "tabata-workouts.json")
    private var database = WorkoutDatabase()
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())

    override val workouts: StateFlow<List<Workout>>
        get() = _workouts.asStateFlow()

    init {
        load()
    }

    override fun save(workout: Workout): Workout {
        return if (workout.id == 0L) {
            insert(workout)
        } else {
            update(workout)
        }
    }

    override fun copy(workout: Workout): Workout {
        val newWorkout = workout.copy(id = 0)
        return insert(newWorkout)
    }

    private fun updateDatabase(workouts: List<Workout>) {
        database = database.copy(workouts = workouts)
        _workouts.value = workouts
        persist()
    }

    private fun update(workout: Workout): Workout {
        val workouts = database.workouts.map {
            if (it.id == workout.id) workout else it
        }
        updateDatabase(workouts)
        return workout
    }

    private fun insert(workout: Workout): Workout {
        val savedWorkout = workout.copy(
            id = database.nextId
        )
        database = database.copy(
            nextId = database.nextId + 1
        )
        updateDatabase(
            database.workouts + savedWorkout
        )
        return savedWorkout
    }

    override fun delete(id: Long) {
        val list = database.workouts.filter {
            it.id != id
        }
        updateDatabase(workouts = list)
    }

    override fun findById(id: Long): Workout? {
        return _workouts.value.firstOrNull {
            it.id == id
        }
    }

    private fun load() {
        if (!file.exists()) {
            persist()
            return
        }

        database = Json.decodeFromString(file.readText())

        _workouts.value = database.workouts
    }

    private fun persist() {
        file.writeText(
            Json {
                prettyPrint = true
            }.encodeToString(database)
        )
    }
}
