package com.example.androidapp.navigation.navigablescreen

import android.os.Build
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate

class CreateNote(
    private val mDayViewModel: DayViewModel,
    private val noteId: Long,
    private val localDate: LocalDate?,
    private val upPress: () -> Unit,
) : NavigableScreen() {

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    override fun View() {
        val note: Note? = mDayViewModel.getNoteById(noteId)
        var titleValue by remember { mutableStateOf(note?.noteTitle ?: "") }
        var noteValue by remember { mutableStateOf(note?.content ?: "") }
        var openDialogClose by remember { mutableStateOf(false) }
        var openDialogDelete by remember { mutableStateOf(false) }

        var isKeyboardVisible by remember { mutableStateOf(false) }

        val view = LocalView.current
        val viewTreeObserver = view.viewTreeObserver
        DisposableEffect(viewTreeObserver) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                isKeyboardVisible = ViewCompat.getRootWindowInsets(view)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            }

            viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }

        Surface(modifier = Modifier.testTag(TestTags.NOTE_EDITOR_VIEW)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            openDialogDelete = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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

                        ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            openDialogClose = true
                        },
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (openDialogClose) {
                        CloseNotePopUp(upPress, text = stringResource(id = R.string.pop_up_window), onSaveChanges = {
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
                            openDialogClose = false
                        }
                    }
                    if (openDialogDelete) {
                        CloseNotePopUp(upPress, text = stringResource(id = R.string.pop_up_delete_window), onSaveChanges = {  if (note != null)
                            mDayViewModel.deleteNote(note) }) {
                            openDialogDelete = false
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
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
                        .clip(MaterialTheme.shapes.small)
                        .testTag(TestTags.NOTE_TITLE_FIELD),
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
                                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSecondaryContainer)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                HorizontalDivider()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()


                )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = if (isKeyboardVisible) 3 / 5f else 1f)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)

                    ) {
                            BasicTextField(
                                value = noteValue,
                                onValueChange = { noteValue = it },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
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
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                textAlign = TextAlign.Justify
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(1.dp)
                                    .verticalScroll(state = rememberScrollState(), enabled = true)
                                    .testTag(TestTags.NOTE_CONTENT_FIELD)
                            )


                    }

                }
            }
        }

    }

}

@Composable
fun CloseNotePopUp(upPress: () -> Unit, text: String, onSaveChanges: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = text,
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground))
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


