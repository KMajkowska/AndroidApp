package com.example.androidapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.androidapp.sounds.ClickSoundManager

const val REQUEST_CODE_PERMISSIONS = 101
const val DELAY = 1250L

val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA
)

class MainActivity : ComponentActivity() {

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        lifecycle.addObserver(ClickSoundManager)

        setContent { UniqrnApp() }

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        Handler().postDelayed({ keepSplashScreen = false }, DELAY)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions are granted
            } else {
                // Permissions not granted. You can notify the user and close the app or disable certain features.
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}

@Preview(showBackground = true)
@Composable
fun UniqrnPreview() {
    UniqrnApp()
}
