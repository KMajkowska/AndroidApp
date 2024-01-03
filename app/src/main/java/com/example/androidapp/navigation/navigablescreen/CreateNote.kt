package com.example.androidapp.navigation.navigablescreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.HorizontalDivider
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
        var openDialog by remember { mutableStateOf(false) }

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
                        tint = MaterialTheme.colorScheme.onBackground
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
                        }
                    }) {
                        openDialog = false
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))



            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                ) {
                    item {
                        BasicTextField(
                            value = titleValue,
                            onValueChange = {
                                val filteredText = it.replace("\n", "")
                                titleValue = filteredText.take(100)
                            },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(4.dp)
                                .clip(MaterialTheme.shapes.small),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    if (titleValue.isEmpty()) {
                                        Text(
                                            text =  stringResource(id = R.string.note_title),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                    item{HorizontalDivider()}

                    item {
                        BasicTextField(
                            value = noteValue,
                            onValueChange = { noteValue = it },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 65.dp)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(4.dp),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    if (noteValue.isEmpty()) {
                                        Text(
                                            text =  stringResource(id = R.string.note_content),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
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
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
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
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .padding(16.dp)
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary
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
            Text(stringResource(id = R.string.pop_up_window))
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