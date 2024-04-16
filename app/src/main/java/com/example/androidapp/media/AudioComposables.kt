package com.example.androidapp.media

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.animations.PlayPauseToggleButtonAnimated
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.nio.ByteBuffer

@Composable
fun AudioPlayer(audioPath: String, modifier: Modifier = Modifier, onLongPress: () -> Unit = {}) {
    val context = LocalContext.current
    val audioUri = getPrivateStorageUri(context, audioPath)
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    val waveform by produceState(initialValue = listOf<Float>(), producer = {
        value = extractWaveformFromAudio(context, audioUri!!)
    })

    DisposableEffect(exoPlayer) {
        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(audioUri!!))
            prepare()
            playWhenReady = false
        }

        onDispose {
            exoPlayer.release()
        }
    }

    WaveformView(
        player = exoPlayer,
        waveform = waveform,
        modifier = modifier,
        onLongPress = onLongPress
    )
}

@Composable
fun WaveformView(
    player: ExoPlayer,
    waveform: List<Float>,
    modifier: Modifier = Modifier,
    onLongPress: () -> Unit = {}
) {
    val isPlaying = remember { mutableStateOf(false) }
    val playbackProgress = remember { mutableFloatStateOf(0f) }
    val currentTime = remember { mutableStateOf("0:00") }

    val waveformColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(player) {
        while (isActive) {
            if (player.duration > 0) {
                playbackProgress.floatValue =
                    player.currentPosition.toFloat() / player.duration.toFloat()
                val seconds = (player.currentPosition / 1000).toInt()
                currentTime.value = String.format("%d:%02d", seconds / 60, seconds % 60)
            }
            delay(10)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {

        Canvas(modifier = modifier.then(Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newPosition = (change.position.x / size.width).coerceIn(0f, 1f)
                    val positionMs = (newPosition * player.duration).toLong()
                    player.seekTo(positionMs)
                }
            }
            .pointerInput(isPlaying) {
                detectTapGestures(onLongPress = { _ -> onLongPress() }) {
                    if (player.duration <= player.currentPosition) {
                        isPlaying.value = true
                        player.seekTo(0)
                    } else {
                        isPlaying.value = !isPlaying.value
                    }
                    player.playWhenReady = isPlaying.value
                }
            })
        ) {
            val maxAmplitude = waveform.maxOrNull() ?: 1f
            val widthPerBar = size.width / waveform.size

            waveform.forEachIndexed { index, amplitude ->
                val normalizedAmplitude = (amplitude / maxAmplitude) * size.height / 2f
                val lineX = widthPerBar * index
                val color =
                    if ((index.toFloat() / waveform.size.toFloat()) < playbackProgress.floatValue) {
                        waveformColor
                    } else {
                        Color.LightGray
                    }

                drawLine(
                    color = color,
                    start = Offset(x = lineX, y = size.height / 2 - normalizedAmplitude),
                    end = Offset(x = lineX, y = size.height / 2 + normalizedAmplitude),
                    strokeWidth = widthPerBar.coerceAtLeast(1f)
                )

            }
        }

        PlayPauseToggleButtonAnimated(
            isPlaying = isPlaying.value,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Text(
            text = currentTime.value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 4.dp)
        )
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

        for (i in byteArray.indices step 256) {
            amplitudes.add((byteArray[i] + byteArray[i + 1]).toFloat())
        }

        extractor.advance()
    }
    extractor.release()

    return amplitudes
}