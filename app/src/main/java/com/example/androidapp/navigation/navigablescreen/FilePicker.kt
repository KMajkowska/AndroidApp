package com.example.androidapp.navigation.navigablescreen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

class FilePicker(
    private val onFilePicked: (Uri, Context) -> Unit,
    private val upPress: () -> Unit,
    private val isExport: Boolean
) : NavigableScreen() {

    @Composable
    override fun View() {
        val context = LocalContext.current
        val exportLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
                uri?.let {
                    onFilePicked(it, context)
                    upPress()
                } ?: run { upPress() }
            }
        val importLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    onFilePicked(it, context)
                    upPress()
                } ?: run { upPress() }
            }

        LaunchedEffect(Unit) {
            if (isExport) {
                exportLauncher.launch(null)
            } else {
                importLauncher.launch("*/*")
            }
        }

        BackHandler {
            upPress()
        }

        Button(onClick = upPress) {
            Text("Cancel")
        }
    }
}