package com.tairone.falamurilo.engine

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class AudioRecorder {

    val isRecording   = MutableStateFlow(false)
    val hasRecording  = MutableStateFlow(false)
    val durationSecs  = MutableStateFlow(0L)

    private var recorder: MediaRecorder? = null
    private var preview: MediaPlayer?    = null
    private var outputFile: File?        = null
    private var startMs = 0L
    private var ticker: Thread? = null

    fun start(file: File) {
        file.parentFile?.mkdirs()
        outputFile = file

        @Suppress("DEPRECATION")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44_100)
            setAudioEncodingBitRate(128_000)
            setOutputFile(file.path)
            prepare()
            start()
        }

        isRecording.value = true
        startMs = System.currentTimeMillis()
        durationSecs.value = 0L
        ticker = Thread {
            while (isRecording.value) {
                durationSecs.value = (System.currentTimeMillis() - startMs) / 1_000
                Thread.sleep(100)
            }
        }.also { it.isDaemon = true; it.start() }
    }

    fun stop() {
        runCatching { recorder?.stop() }
        recorder?.release()
        recorder = null
        isRecording.value = false
        ticker = null
        if (outputFile?.exists() == true) hasRecording.value = true
    }

    fun playPreview(file: File) {
        preview?.stop(); preview?.release()
        runCatching {
            preview = MediaPlayer().apply {
                setDataSource(file.path); prepare(); start()
            }
        }
    }

    fun reset() {
        stop()
        preview?.stop(); preview?.release(); preview = null
        hasRecording.value = false
        durationSecs.value = 0L
        outputFile = null
    }
}
