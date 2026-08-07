package com.claudemr.tabata.model

import kotlinx.serialization.Serializable

@Serializable
data class Duration(
    val seconds: Int = 0
) {
    fun toFrenchTts(): String {
        val parts = mutableListOf<String>()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        if (minutes > 0) {
            parts += "${minutes} minutes"
            if (remainingSeconds > 0)
                parts += "et"
        }

        if (remainingSeconds > 0 || parts.isEmpty())
            parts += "${remainingSeconds} secondes"

        return parts.joinToString(" ")
    }

    override fun toString(): String {
        val parts = mutableListOf<String>()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        if (minutes > 0)
            parts += "${minutes} min"

        if (remainingSeconds > 0 || parts.isEmpty())
            parts += "${remainingSeconds} s"

        return parts.joinToString(" ")
    }

}