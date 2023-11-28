package com.example.androidapp.navigablescreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.MainActivity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.ui.theme.AndroidAppTheme
import com.example.androidapp.ui.theme.Blue
import com.example.androidapp.ui.theme.Red

const val TITLE_TEXT : String = "TITLE"
const val NOTE_TEXT : String = "NOTE"

class CreateNote() : ComponentActivity(){
    private val mDayViewModel: DayViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppTheme {
                val noteId = intent.getLongExtra("noteId", -1)
                val existingNote = mDayViewModel.getNoteById(noteId)
                AddBackgroundToComposables( {
                    View(existingNote)
                })
            }
        }
    }

    @Composable
    fun View(existingNote: Note?=null) {
        var titleValue by remember { mutableStateOf(existingNote?.noteTitle ?: "") }
        var noteValue by remember { mutableStateOf(existingNote?.content ?: "") }
        val context = LocalContext.current

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
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
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
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = titleValue,
                onValueChange = { titleValue = it },
                label = { Text("Title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
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
                    label = { Text("Note") },
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
                            if (existingNote == null) {
                                // Create a new note
                                val newNote = Note(
                                    noteTitle = titleValue,
                                    content = noteValue,
                                )
                                mDayViewModel.addNewNote(newNote)
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                // Update the existing note
                                val updatedNote = existingNote.copy(
                                    noteTitle = titleValue,
                                    content = noteValue
                                )
                                mDayViewModel.updateNote(updatedNote)
                            }
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
                            if (existingNote != null) {
                                mDayViewModel.deleteNote(existingNote)
                            }
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
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