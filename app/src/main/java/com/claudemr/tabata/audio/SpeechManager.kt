package com.claudemr.tabata.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale
import java.util.UUID

class SpeechManager(
    context: Context
) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(
        context,
        this
    )

    private var initialized = false
    private val ready = CompletableDeferred<Unit>()
    private val callbacks =
        mutableMapOf<String, () -> Unit>()

    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) {
            return
        }

        val result = tts.setLanguage(Locale.FRENCH)

        if (
            result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            return
        }

        initialized = true

        tts.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(
                    utteranceId: String?
                ) {
                }

                override fun onDone(
                    utteranceId: String?
                ) {
                    utteranceId ?: return

                    if (utteranceId == "warmup") {
                        if (!ready.isCompleted) {
                            Handler(
                                Looper.getMainLooper()
                            ).post {
                                ready.complete(Unit)
                            }
                        }
                        return
                    }

                    callbacks.remove(utteranceId)?.let { callback ->
                        Handler(
                            Looper.getMainLooper()
                        ).post {
                            callback()
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(
                    utteranceId: String?
                ) {
                    callbacks.remove(utteranceId)
                }
            }
        )

        tts.playSilentUtterance(
            1000,
            TextToSpeech.QUEUE_FLUSH,
            "warmup"
        )
    }

    fun say(
        text: String,
        onDone: (() -> Unit)? = null
    ) {
        if (!initialized) {
            return
        }

        val utteranceId = UUID.randomUUID().toString()

        if (onDone != null) {
            callbacks[utteranceId] = onDone
        }

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId
        )
    }

    suspend fun awaitReady() {
        ready.await()
    }

    fun initialize() {
        // Do nothing, but forces the TTS to initialize
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}