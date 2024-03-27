package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapp.database.model.savables.Note
import com.example.androidapp.database.viewmodel.DayViewModel

class ChatNotes(private val mDayViewModel: DayViewModel) : NavigableScreen() {
    @Composable
    override fun View() {
        Scaffold(
            topBar = { /* ... */ },
            // bottomBar is used for the message creation UI
            bottomBar = {
                MessageCreationRow(
                    onAttachmentClick = { /* Handle attachment click */ },
                    onCameraClick = { /* Handle camera click */ },
                    onSendClick = { message -> /* Handle sending a message */ }
                )
            }
        ) { innerPadding ->
            // Content of the screen, i.e., messages list goes here.
            // Remember to provide innerPadding to the content composable to respect Scaffold's bottomBar padding.
            ChatContent(paddingValues = innerPadding)
        }

    }

    @Composable
    fun ChatContent(paddingValues: PaddingValues) {
        val notes = mDayViewModel.allNotes.observeAsState(initial = listOf()).value

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(items = notes) { note ->
                MessageRow(note = note)
            }
        }
    }
}

@Composable
fun MessageRow(note: Note) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MessageCreationRow(
    modifier: Modifier = Modifier,
    onAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onAttachmentClick() }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
            }

            IconButton(onClick = { onCameraClick() }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message") },
                singleLine = true
            )

            IconButton(onClick = {
                if (text.isNotBlank()) {
                    onSendClick(text)
                    text = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
