package com.example.androidapp.sounds

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.androidapp.R

object ClickSoundManager : DefaultLifecycleObserver {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStart(owner: LifecycleOwner) {
        val context = owner as? Context ?: return
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

    override fun onDestroy(owner: LifecycleOwner) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
