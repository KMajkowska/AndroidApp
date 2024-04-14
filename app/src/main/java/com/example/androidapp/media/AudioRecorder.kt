package com.example.androidapp.media

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var filePath: String = ""

    fun startRecording() {
        filePath = "${generateUUID()}.mp3"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(filePath)
            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }

    fun getFilePath() = filePath
}
