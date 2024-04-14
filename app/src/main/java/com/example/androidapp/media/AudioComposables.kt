package com.example.androidapp.media

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.nio.ByteBuffer

@Composable
fun AudioPlayer(audioPath: String) {
    val context = LocalContext.current
    val audioUri = getPrivateStorageUri(context, audioPath)

    val waveform by produceState(initialValue = listOf<Float>(), producer = {
        value = extractWaveformFromAudio(context, audioUri!!)
    })

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(audioUri!!)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(key1 = exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PlayerControlView(exoPlayer = exoPlayer)
        Spacer(modifier = Modifier.height(16.dp))
        WaveformView(waveformData = waveform, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun WaveformView(waveformData: List<Float>, modifier: Modifier = Modifier) {
    val waveformColor = Color(0xFF76FF03)

    Canvas(modifier = modifier.height(100.dp).fillMaxWidth().background(Color.Black)) {
        val maxAmplitude = waveformData.maxOrNull() ?: 1f
        val widthPerBar = size.width / waveformData.size
        val height = size.height

        waveformData.forEachIndexed { index, amplitude ->
            val normalizedAmplitude = (amplitude / maxAmplitude) * height / 2f
            val lineX = widthPerBar * index
            drawLine(
                color = waveformColor,
                start = Offset(x = lineX, y = height / 2 - normalizedAmplitude),
                end = Offset(x = lineX, y = height / 2 + normalizedAmplitude),
                strokeWidth = widthPerBar.coerceAtLeast(1f)
            )
        }
    }
}

@Composable
fun PlayerControlView(exoPlayer: ExoPlayer) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(onClick = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() }) {
            Text(if (exoPlayer.isPlaying) "Pause" else "Play")
        }
    }
}

private fun extractWaveformFromAudio(context: Context, audioUri: Uri): List<Float> {
    val extractor = MediaExtractor()
    extractor.setDataSource(context, audioUri, null)
    val format = extractor.getTrackFormat(0)
    val maxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
    val byteArray = ByteArray(maxInputSize)
    val amplitudes = mutableListOf<Float>()

    extractor.selectTrack(0)
    while (true) {
        val sampleSize = extractor.readSampleData(ByteBuffer.wrap(byteArray), 0)

        if (sampleSize < 0) {
            break
        }

        for (i in byteArray.indices step 2) {
            amplitudes.add((byteArray[i] + byteArray[i + 1] * 256).toFloat())
        }

        extractor.advance()
    }
    extractor.release()

    return amplitudes
}