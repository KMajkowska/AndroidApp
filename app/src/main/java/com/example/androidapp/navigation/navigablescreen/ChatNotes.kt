package com.example.androidapp.navigation.navigablescreen

import android.view.MotionEvent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.media.AudioPlayer
import com.example.androidapp.media.AudioRecorder
import com.example.androidapp.media.ClickableVideoPlayer
import com.example.androidapp.media.MimeTypeEnum
import com.example.androidapp.media.imagePainter
import com.example.androidapp.media.mediaPicker

class ChatNotes(
    private val noteForeignId: Long,
    private val mDayViewModel: DayViewModel,
    private val upPress: () -> Unit,
) : NavigableScreen() {
    @Composable
    override fun View() {
        val visualMediaPicker = mediaPicker(context = LocalContext.current) { path, mimeType ->
            if (path == null || mimeType == null)
                return@mediaPicker

            mDayViewModel.addConnectedToNoteText(
                ConnectedToNote(
                    contentOrPath = path,
                    noteForeignId = noteForeignId,
                    id = null,
                    mimeType = mimeType
                )
            )
        }

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
                        visualMediaPicker.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    },
                    audioRecorder = AudioRecorder(),
                    onAudioRecorderFinished = { path ->
                        mDayViewModel.addConnectedToNoteText(
                            ConnectedToNote(
                                id = null,
                                contentOrPath = path,
                                noteForeignId = noteForeignId,
                                mimeType = MimeTypeEnum.SOUND.toString()
                            )
                        )
                    },
                    onCameraClick = { },
                    onSendClick = { content ->
                        mDayViewModel.addConnectedToNoteText(
                            ConnectedToNote(
                                id = null,
                                contentOrPath = content,
                                noteForeignId = noteForeignId,
                                mimeType = MimeTypeEnum.TEXT.toString()
                            )
                        )
                    }
                )
            }
        ) { innerPadding ->
            ChatContent(paddingValues = innerPadding)
        }

    }

    @Composable
    fun ChatContent(paddingValues: PaddingValues) {

        val connectedToDays = mDayViewModel
            .getConnectedToNoteByNoteId(noteForeignId)
            .observeAsState(initial = listOf())
            .value

        val stableConnectedToDays = remember(connectedToDays) { connectedToDays }

        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            reverseLayout = true
        ) {
            items(items = stableConnectedToDays, key = { it.id!! }) { connectedToDay ->
                MessageRow(connectedToNote = connectedToDay)
            }
        }
    }
}

@Composable
fun MessageRow(connectedToNote: ConnectedToNote) {
    val messageModifier = Modifier
        .size(250.dp)
        .clip(RoundedCornerShape(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            modifier = Modifier.padding(4.dp)
        ) {
            when (MimeTypeEnum.getMimeTypeEnumFromString(connectedToNote.mimeType)) {
                MimeTypeEnum.TEXT -> {
                    Text(
                        text = connectedToNote.contentOrPath,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                MimeTypeEnum.IMAGE -> {
                    val imagePainter = imagePainter(imagePath = connectedToNote.contentOrPath)

                    Image(
                        painter = imagePainter,
                        contentDescription = "Note Image",
                        contentScale = ContentScale.Crop,
                        modifier = messageModifier
                    )
                }

                MimeTypeEnum.VIDEO -> {
                    ClickableVideoPlayer(
                        videoPath = connectedToNote.contentOrPath,
                        modifier = messageModifier
                    )
                }
                
                MimeTypeEnum.SOUND -> {
                    AudioPlayer(audioPath = connectedToNote.contentOrPath)
                }

                else -> {
                    Text(
                        text = "ŁOT DE HEEEL",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = messageModifier
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageCreationRow(
    modifier: Modifier = Modifier,
    audioRecorder: AudioRecorder,
    onAudioRecorderFinished: (String) -> Unit,
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

            IconButton(onClick = { /* This is required but will not be used */ },
                modifier = Modifier.pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            audioRecorder.startRecording()
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            audioRecorder.stopRecording()
                            onAudioRecorderFinished(audioRecorder.getFilePath())
                        }
                    }
                    true // Return true to capture the event
                }) {
                Icon(Icons.Default.Mic, contentDescription = "Record")
            }

            IconButton(onClick = onAttachmentClick) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
            }

            IconButton(onClick = onCameraClick) {
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

