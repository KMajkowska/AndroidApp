package com.example.androidapp.media

import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var filePath: String? = null

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
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording(): String? {
        val returnFilePath = filePath

        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            filePath = null
        }

        return returnFilePath
    }
}
