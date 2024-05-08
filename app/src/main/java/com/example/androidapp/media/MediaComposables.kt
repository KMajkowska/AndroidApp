package com.example.androidapp.media

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.androidapp.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

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
