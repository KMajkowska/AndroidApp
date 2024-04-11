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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.androidapp.database.model.ConnectedToDay
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel

class ChatNotes(
    private val mDayViewModel: DayViewModel,
    private val upPress: () -> Unit,
) : NavigableScreen() {
    @Composable
    override fun View() {
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    title = "Daily note",
                    onNavigationClick = upPress,
                    onOverflowClick = { /* Handle overflow menu click */ }
                )
            },
            bottomBar = {
                MessageCreationRow(
                    onAttachmentClick = {
                    },
                    onCameraClick = {
                    },
                    onSendClick = { content ->
                        mDayViewModel.addNewNote(Note(content = content))
                    }
                )
            }
        ) { innerPadding ->
            ChatContent(paddingValues = innerPadding)
        }

    }

    @Composable
    fun ChatContent(paddingValues: PaddingValues) {
        val connectedToDays = mDayViewModel.allConnectedToDay.observeAsState(initial = listOf()).value

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(items = connectedToDays) { connectedToDay ->
                MessageRow(connectedToDay = connectedToDay)
            }
        }
    }
}

@Composable
fun MessageRow(connectedToDay: ConnectedToDay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            modifier = Modifier.padding(4.dp)
        ) {
            if (connectedToDay is Note) {
                Text(
                    text = connectedToDay.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = "ŁOT DE HEEEL",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(title: String, onNavigationClick: () -> Unit, onOverflowClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationClick() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            // Actions you might want to add (like search, etc.)
            IconButton(onClick = { onOverflowClick() }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    )
}

