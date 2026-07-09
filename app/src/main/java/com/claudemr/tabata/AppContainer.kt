package com.claudemr.tabata

import android.content.Context
import com.claudemr.tabata.audio.SpeechManager
import com.claudemr.tabata.repository.JsonWorkoutRepository

class AppContainer(
    private val context: Context
) {
    val workoutRepository = JsonWorkoutRepository(context)
    val speechManager = SpeechManager(context)
}