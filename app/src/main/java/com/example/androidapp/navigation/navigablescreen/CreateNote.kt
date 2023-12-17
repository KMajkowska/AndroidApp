package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androidapp.R
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.ui.theme.Blue
import com.example.androidapp.ui.theme.Red
import java.time.LocalDate

class CreateNote(
    private val mDayViewModel: DayViewModel,
    private val noteId: Long,
    private val localDate: LocalDate?,
    private val upPress: () -> Unit,
) : NavigableScreen() {
    @Composable
    override fun View() {
        val note: Note? = mDayViewModel.getNoteById(noteId)
        var titleValue by remember { mutableStateOf(note?.noteTitle ?: "") }
        var noteValue by remember { mutableStateOf(note?.content ?: "") }
        var openDialog by remember {
            mutableStateOf(false)
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = {
                              openDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                if (openDialog) {
                    CloseNotePopUp(upPress, onSaveChanges = {
                        if (note == null) {
                            val newNote = Note(
                                noteTitle = titleValue,
                                content = noteValue
                            )
                            if (localDate != null)
                                newNote.noteDate = localDate
                            mDayViewModel.addNewNote(newNote)
                        } else {
                            val updatedNote = note.copy(
                                noteTitle = titleValue,
                                content = noteValue
                            )
                            mDayViewModel.updateNote(updatedNote)
                        }}) {
                        openDialog = false
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = titleValue,
                onValueChange = {
                    val filteredText = it.replace("\n", "")
                    titleValue = filteredText.take(50)
                },
                label = { Text(stringResource(id = R.string.note_title)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                OutlinedTextField(
                    value = noteValue,
                    onValueChange = { noteValue = it },
                    label = { Text(stringResource(id = R.string.note_content)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 65.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                ) {

                    IconButton(
                        onClick = {
                            if (note == null) {
                                val newNote = Note(
                                    noteTitle = titleValue,
                                    content = noteValue
                                )

                                if (localDate != null)
                                    newNote.noteDate = localDate
                                mDayViewModel.addNewNote(newNote)
                            } else {
                                val updatedNote = note.copy(
                                    noteTitle = titleValue,
                                    content = noteValue
                                )

                                mDayViewModel.updateNote(updatedNote)
                            }
                            upPress()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Blue, CircleShape)
                            .padding(16.dp)
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            if (note != null)
                                mDayViewModel.deleteNote(note)
                            upPress()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Red, CircleShape)
                            .padding(16.dp)
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloseNotePopUp(upPress: () -> Unit, onSaveChanges: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Do you want to save the changes in the note?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveChanges()
                    upPress()
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    upPress()
                    onDismiss()
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}

