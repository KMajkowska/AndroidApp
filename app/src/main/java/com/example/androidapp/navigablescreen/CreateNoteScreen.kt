package com.example.androidapp.navigablescreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

class CreateNoteScreen : NavigableScreen() {
    @Composable
    override fun View() {
        Text(
            text = "Hello world!"
        )
    }

    override val screenName: String
        get() = "Create note"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Create
    }
}