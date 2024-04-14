package com.example.androidapp.media

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.androidapp.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.nio.ByteBuffer

@Composable
fun mediaPicker(context: Context, saveUrlStringAndMimeTypeFun: (String?, String?) -> Unit) =
    rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            saveUrlStringAndMimeTypeFun(
                saveMediaToInternalStorage(context, uri),
                context.contentResolver.getType(uri)
            )
        }
    }

@Composable
fun imagePainter(imagePath: String): Painter {
    val imageUri = getPrivateStorageUri(LocalContext.current, imagePath)
        ?: return painterResource(id = R.drawable.pen)

    return rememberAsyncImagePainter(model = imageUri)
}

@Composable
fun ClickableVideoPlayer(videoPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val videoUri = getPrivateStorageUri(context, videoPath)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri!!))
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    controllerHideOnTouch = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            },
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
fun IconToggleButton(isPlaying: Boolean, onClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(isPlaying) }
    val animationDuration = 100

    LaunchedEffect(isPlaying) {
        isVisible = !isPlaying
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "alpha"
    )

    AnimatedVisibility(
        visible = isVisible || !isPlaying,
        enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDuration))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White.copy(alpha = alpha),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
