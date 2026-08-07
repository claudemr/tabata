package com.claudemr.tabata.state

import com.claudemr.tabata.model.WorkoutStep

data class WorkoutEditorState(
    val id: Long = 0,
    var name: String = "",
    var description: String = "",
    val nbTurns: Int = 1,
    val steps: List<WorkoutStep> = emptyList()
)
