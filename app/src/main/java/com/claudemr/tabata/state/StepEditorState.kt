package com.claudemr.tabata.state

import com.claudemr.tabata.model.Duration

data class StepEditorState(
    val type: StepType = StepType.MANUAL,
    val name: String = "",
    val description: String = "",
    val rest: Duration = Duration(seconds = 60),
    val nbSets: Int = 1,
    val nbReps: Int = 15,
    val duration: Duration = Duration(seconds = 60)
)