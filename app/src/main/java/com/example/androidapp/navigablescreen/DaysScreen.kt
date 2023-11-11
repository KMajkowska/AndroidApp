package com.example.androidapp.navigablescreen

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider

class DaysScreen : NavigableScreen() {

    override val screenName: String
        get() = "Days"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Create
    }

    @Composable
    override fun ViewWithBackground() {
        AddBackgroundToComposables({ Calendar() }, { View() })
    }

    @Composable
    override fun View() {
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
        ) {
            item {
                val note by remember { mutableStateOf(Note("Title", "Content")) }
                // TODO: actual note from some db! and update it in the function below

                NoteTextEditor(note)
            }
            items(50) {
                Text(
                    text = "Item $it",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                HorizontalDivider()
            }
        }
    }

    @Composable
    fun Calendar() {
        Row(
            modifier = Modifier
                .padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {

            AndroidView(
                { CalendarView(it) }, // this 'it' is the current context!
                modifier = Modifier.wrapContentWidth(),
                update = { // TODO: implement}
                }
            )
        }
    }
}

@Composable
fun NotePreview(note: Note, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(8.dp)
            .clickable {
                onEditClick()
            }
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NoteTextEditor(note: Note) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNote by remember { mutableStateOf(note) }

    if (isEditing) {
        // Open the text editor
        TextEditor(note = editedNote, onNoteChanged = { editedNote = it }) {
            isEditing = false
        }
    } else {
        // Show the note preview
        NotePreview(note = editedNote) {
            isEditing = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextEditor(
    note: Note,
    onNoteChanged: (Note) -> Unit,
    onCloseEditor: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = {
                title = it
                onNoteChanged(Note(title, content))
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = content,
            onValueChange = {
                content = it
                onNoteChanged(Note(title, content))
            },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Add any other buttons or actions you need here

            // Close editor button
            Button(
                onClick = {
                    onCloseEditor()
                    keyboardController?.hide()
                }
            ) {
                Text("Close Editor")
            }
        }
    }
}

data class Note(val title: String, var content: String)
