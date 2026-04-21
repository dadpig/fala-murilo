package com.tairone.falamurilo.engine

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.tairone.falamurilo.models.AppLanguage
import java.io.File
import java.util.Locale

class SpeechEngine(context: Context) {

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
        }
    }

    fun speak(text: String, language: AppLanguage) {
        stop()
        if (!ready) return
        tts?.language = Locale.forLanguageTag(language.code)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun playAudio(file: File) {
        stop()
        runCatching {
            player = MediaPlayer().apply {
                setDataSource(file.path)
                prepare()
                start()
            }
        }
    }

    fun stop() {
        tts?.stop()
        player?.stop()
        player?.release()
        player = null
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
