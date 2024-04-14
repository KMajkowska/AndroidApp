package com.example.androidapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

const val REQUEST_AUDIO_PERMISSION = 200
const val REQUEST_VIDEO_PERMISSION = 201

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestPermission(Manifest.permission.RECORD_AUDIO, REQUEST_AUDIO_PERMISSION)
        requestPermission(Manifest.permission.CAMERA, REQUEST_VIDEO_PERMISSION)

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContent {
            UniqrnApp()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION -> requestedPermissionAction(grantResults)
            REQUEST_VIDEO_PERMISSION -> requestedPermissionAction(grantResults)
        }
    }

    private fun requestPermission(permissionName: String, permissionNumber: Int) {
        if (ContextCompat.checkSelfPermission(this, permissionName) != permissionNumber) {
            ActivityCompat.requestPermissions(this, arrayOf(permissionName), permissionNumber)
        }
    }

    private fun requestedPermissionAction(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // granted
        } else {
            // denied
        }
    }

}

@Preview(showBackground = true)
@Composable
fun UniqrnPreview() {
    UniqrnApp()
}
