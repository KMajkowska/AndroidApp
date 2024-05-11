package com.example.androidapp.sounds

import android.content.Context
import android.media.MediaPlayer
import com.example.androidapp.R

object ClickSoundManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.click)
    }

    fun playClickSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.prepare()
            }
            it.start()
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
