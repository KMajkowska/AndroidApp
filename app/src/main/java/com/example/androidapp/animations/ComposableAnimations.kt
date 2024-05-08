package com.example.androidapp.animations

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp


@Composable
fun PlayPauseToggleButtonAnimated(isPlaying: Boolean, color: Color) {
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
        exit = fadeOut(animationSpec = tween(durationMillis = animationDuration)),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = color.copy(alpha = alpha),
                modifier = Modifier.size(48.dp)
            )

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatedIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onActionDown: () -> Unit = {},
    onActionUp: () -> Unit = {},
    innerComposable: @Composable () -> Unit = {}
) {
    val selected = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (selected.value) 2f else 1f, label = "selectedValue")

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .scale(scale.value)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        selected.value = true
                        onActionDown()
                    }

                    MotionEvent.ACTION_UP -> {
                        selected.value = false
                        onActionUp()
                    }
                }
                true
            }
            .then(modifier)
    ) {
        innerComposable()
    }
}
