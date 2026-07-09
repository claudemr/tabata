package com.claudemr.tabata.model

import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0
) {
    fun totalSeconds(): Int =
        hours * 3600 + minutes * 60 + seconds

    override fun toString(): String {

        val parts = mutableListOf<String>()

        if (hours > 0)
            parts += "${hours} h"

        if (minutes > 0)
            parts += "${minutes} min"

        if (seconds > 0 || parts.isEmpty())
            parts += "${seconds} s"

        return parts.joinToString(" ")
    }
}