package com.claudemr.tabata.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import java.io.File
import com.claudemr.tabata.model.Workout


class JsonWorkoutRepository(
    private val context: Context
) : IWorkoutRepository {

    private val _file = File(context.filesDir, "tabata-workouts.json")
    private var _database = WorkoutDatabase()
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    private val _json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

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
        _database = _database.copy(workouts = workouts)
        _workouts.value = workouts
        persist()
    }

    private fun update(workout: Workout): Workout {
        val workouts = _database.workouts.map {
            if (it.id == workout.id) workout else it
        }
        updateDatabase(workouts)
        return workout
    }

    private fun insert(workout: Workout): Workout {
        val savedWorkout = workout.copy(
            id = _database.nextId
        )
        _database = _database.copy(
            nextId = _database.nextId + 1
        )
        updateDatabase(
            _database.workouts + savedWorkout
        )
        return savedWorkout
    }

    override fun delete(id: Long) {
        val list = _database.workouts.filter {
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
        if (!_file.exists()) {
            persist()
            return
        }



        _database = try {
            val loadedDatabase =
                _json.decodeFromString<WorkoutDatabase>(
                    _file.readText()
                )

            var nextId = 1L

            val workouts = loadedDatabase.workouts.map { workout ->
                workout.copy(id = nextId++)
            }

            WorkoutDatabase(
                nextId = nextId,
                workouts = workouts
            )

        } catch (e: Exception) {
            Log.e(
                "TABATA",
                "Impossible de charger la configuration JSON. " +
                        "Le fichier va être supprimé.",
                e
            )

            if (!_file.delete()) {
                Log.e("TABATA", "Impossible de supprimer le fichier JSON.")
            }

            WorkoutDatabase()
        }

        _workouts.value = _database.workouts
    }

    private fun persist() {
        _file.writeText(_json.encodeToString(_database))
    }

    override fun exportJson(): String {
        return _json.encodeToString(_database)
    }

    override fun importJson(json: String) : ImportResult {
        return try {
            val importedWorkouts =
                _json.decodeFromString<List<Workout>>(json)

            var nextId = _database.nextId

            val workoutsToAdd = importedWorkouts.map { imported ->
                Workout(
                    id = nextId++,
                    name = imported.name,
                    description = imported.description,
                    nbTurns = imported.nbTurns,
                    steps = imported.steps
                )
            }

            val workouts = _database.workouts + workoutsToAdd

            _database = _database.copy(
                workouts = workouts,
                nextId = nextId
            )

            _workouts.value = workouts

            persist()

            ImportResult.Success

        } catch (e: SerializationException) {
            ImportResult.Error("Le fichier n'est pas un fichier Tabata valide.")
        } catch (e: Exception) {
            ImportResult.Error("Impossible d'importer ce fichier.")
        }
    }
}
