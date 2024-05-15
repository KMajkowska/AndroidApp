package com.example.androidapp.media

import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var filePath: String? = null
    private var startTime: Long = 0

    fun startRecording(getAudioFilePath: (String) -> File) {
        if (isRecording)
            return

        val tempFileName = "${generateUUID()}.mp3"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(getAudioFilePath(tempFileName))

            try {
                prepare()
                start()
                filePath = tempFileName
                startTime = System.currentTimeMillis()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
                releaseMediaRecorder()
            }
        }
    }

    fun stopRecording(): String? {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        if (isRecording) {
            try {
                mediaRecorder?.stop()
                return if (duration < 1000) {
                    val file = filePath?.let { File(it) }
                    if (file != null) {
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    null
                } else {
                    filePath
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()

            } finally {
                releaseMediaRecorder()
            }
        } else {
           return null
        }

        return filePath
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
        filePath = null
    }
}
