package com.example.androidapp.media

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFileName = "${generateUUID()}.jpg"
    val outputOptions = ImageCapture
        .OutputFileOptions
        .Builder(
            getPrivateStorageFileFromFilePath(
                context,
                photoFileName
            )
        ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageSaved(photoFileName)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

@Composable
fun cameraPreview(
    modifier: Modifier = Modifier,
): ImageCapture {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    val preview = remember { Preview.Builder().build() }

    val previewView = remember { mutableStateOf<PreviewView?>(null) }
    val cameraProvider = remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(lifecycleOwner) {
        val listener = {
            val provider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.value = provider
            try {
                provider.unbindAll()
                if (provider.hasCamera(cameraSelector)) {
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } else {
                    Log.e("CameraPreview", "No back camera found")
                }
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }

        cameraProviderFuture.addListener({ listener() }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider.value?.unbindAll()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                previewView.value = this
            }
        },
        update = { view ->
            preview.setSurfaceProvider(view.surfaceProvider)
        }
    )

    return imageCapture
}
