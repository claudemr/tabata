package com.claudemr.tabata.state

import com.claudemr.tabata.model.Duration

data class StepEditorState(
    val type: StepType = StepType.MANUAL,
    val title: String = "",
    val description: String = "",
    val duration: Duration = Duration(seconds = 30)
)