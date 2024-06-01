package com.example.androidapp.navigation.navigablescreen

import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.androidapp.Dialog
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.animations.AnimatedIconButton
import com.example.androidapp.buttonsEffects.bounceClick
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.media.AudioPlayer
import com.example.androidapp.media.AudioRecorder
import com.example.androidapp.media.ClickableVideoPlayer
import com.example.androidapp.media.MimeTypeEnum
import com.example.androidapp.media.cameraPreview
import com.example.androidapp.media.getPrivateStorageFileFromFilePath
import com.example.androidapp.media.imagePainter
import com.example.androidapp.media.mediaPicker
import com.example.androidapp.media.takePicture
import com.example.androidapp.sounds.ClickSoundManager

const val DEFAULT_LONG_VALUE = -1L

class ChatNotes(
    private val noteForeignId: Long,
    private val mDayViewModel: DayViewModel,
    private val upPress: () -> Unit,
) : NavigableScreen() {

    private val audioRecorder: AudioRecorder = AudioRecorder()

    private fun addToDatabase(mimeTypeString: String, contentOrPath: String) {
        mDayViewModel.addConnectedToNote(
            ConnectedToNote(
                id = null,
                contentOrPath = contentOrPath,
                noteForeignId = noteForeignId,
                mimeType = mimeTypeString
            )
        )
    }

    @Composable
    override fun View() {
        if (noteForeignId == DEFAULT_LONG_VALUE) {
            return
        }

        var showCamera by remember { mutableStateOf(false) }

        if (showCamera) {
            ConfiguredCameraPreview { showCamera = false }
        } else {
            ShowChat { showCamera = true }
        }
    }

    @Composable
    fun ConfiguredCameraPreview(
        onTakePicture: () -> Unit
    ) {
        val context = LocalContext.current
        val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
        val cameraReady = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            cameraPreview(Modifier.fillMaxSize()).let { capture ->
                imageCapture.value = capture
                cameraReady.value = true
            }

            if (!cameraReady.value || imageCapture.value == null) {
                return
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(69.dp), // nice
                contentAlignment = Alignment.BottomCenter
            ) {
                IconButton(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    onClick = {
                        takePicture(
                            context = context,
                            imageCapture = imageCapture.value!!,
                            onImageSaved = { path ->
                                addToDatabase(MimeTypeEnum.IMAGE.toString(), path)
                                onTakePicture()
                            },
                            onError = { exception ->
                                Log.e("Error", exception.localizedMessage ?: "Unknown error")
                            }
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        tint = Color.White,
                        contentDescription = "TakeAPicture",
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }
        }
    }

    @Composable
    fun ShowChat(onShowCamera: () -> Unit) {
        var title by remember {
            mutableStateOf(
                mDayViewModel.getNoteById(noteForeignId)!!.noteTitle
            )
        }

        var isDeleteDialogForNoteOpen by remember { mutableStateOf(false) }

        val visualMediaPicker = mediaPicker(context = LocalContext.current) { path, mimeType ->
            if (path != null && mimeType != null) {
                addToDatabase(
                    mimeTypeString = mimeType,
                    contentOrPath = path
                )
            }
        }

        DeleteDialog(
            isShown = isDeleteDialogForNoteOpen,
            onDismissDelete = { isDeleteDialogForNoteOpen = false }
        ) {
            if (noteForeignId != DEFAULT_LONG_VALUE) {
                mDayViewModel.deleteNoteEntity(mDayViewModel.getNoteById(noteForeignId)!!)
            }

            isDeleteDialogForNoteOpen = false
            upPress()
        }

        LaunchedEffect(title) {
            mDayViewModel.changeNoteTitle(noteForeignId, title)
        }

        Scaffold(
            modifier = Modifier.testTag(TestTags.NOTE_EDITOR_VIEW),
            topBar = {
                CustomTopAppBar(
                    title = title,
                    onTitleChanged = { title = it },
                    onNavigationClick = upPress,
                    onDelete = { isDeleteDialogForNoteOpen = true }
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
                        ClickSoundManager.playClickSound()
                    },
                    audioRecorder = audioRecorder,
                    onAudioRecorderFinished = { path ->
                        if (path != null) {
                            addToDatabase(
                                mimeTypeString = MimeTypeEnum.SOUND.toString(),
                                contentOrPath = path
                            )
                        }
                    },
                    onCameraClick = onShowCamera,
                    onSendClick = { content ->
                        addToDatabase(
                            mimeTypeString = MimeTypeEnum.TEXT.toString(),
                            contentOrPath = content
                        )
                        ClickSoundManager.playClickSound()
                    }
                )
            }
        ) { innerPadding ->
            ChatContent(paddingValues = innerPadding)
        }
    }

    @Composable
    fun ChatContent(paddingValues: PaddingValues) {

        var isDeleteDialogShown by remember { mutableStateOf(false) }
        var currentItemToDelete by remember { mutableLongStateOf(DEFAULT_LONG_VALUE) }

        val connectedToDays = mDayViewModel
            .getConnectedToNoteByNoteId(noteForeignId)
            .observeAsState(initial = listOf())
            .value

        val stableConnectedToDays = remember(connectedToDays) { connectedToDays }

        DeleteDialog(
            isShown = isDeleteDialogShown,
            onDismissDelete = {
                isDeleteDialogShown = false
                currentItemToDelete = DEFAULT_LONG_VALUE
            }
        ) {
            if (currentItemToDelete != DEFAULT_LONG_VALUE) {
                mDayViewModel.deleteConnectedToNoteById(currentItemToDelete)
            }

            isDeleteDialogShown = false
            currentItemToDelete = DEFAULT_LONG_VALUE
        }

        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            reverseLayout = true
        ) {
            items(
                items = stableConnectedToDays,
                key = { it.id!! }
            ) { connectedToNote ->

                val deleteDialog = {
                    currentItemToDelete = connectedToNote.id!!
                    isDeleteDialogShown = true
                }

                MessageRow(
                    modifier = Modifier
                        .pointerInput(isDeleteDialogShown) {
                            detectTapGestures(
                                onLongPress = { _ -> deleteDialog() }
                            )
                        },
                    connectedToNote = connectedToNote,
                    onLongPress = deleteDialog
                )
            }
        }
    }
}

@Composable
fun MessageRow(
    modifier: Modifier = Modifier,
    connectedToNote: ConnectedToNote,
    onLongPress: () -> Unit = {}
) {
    val messageModifier = Modifier
        .size(250.dp)
        .clip(RoundedCornerShape(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.End
    ) {
        Card(modifier = Modifier.padding(4.dp)) {
            when (MimeTypeEnum.getMimeTypeEnumFromString(connectedToNote.mimeType)) {
                MimeTypeEnum.TEXT -> {
                    Text(
                        text = connectedToNote.contentOrPath,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                MimeTypeEnum.IMAGE -> {
                    //val showDialog = remember { mutableStateOf(false) }
                    Image(
                        painter = imagePainter(imagePath = connectedToNote.contentOrPath),
                        contentDescription = "Note Image",
                        contentScale = ContentScale.Crop,
                        modifier = messageModifier/*.clickable {
                            // Open the image in full screen or perform any other action here
                        }*/
                    )
                }

                MimeTypeEnum.VIDEO -> {
                    ClickableVideoPlayer(
                        videoPath = connectedToNote.contentOrPath,
                        modifier = messageModifier
                    )
                }

                MimeTypeEnum.SOUND -> {
                    AudioPlayer(
                        audioPath = connectedToNote.contentOrPath,
                        modifier = modifier,
                        onLongPress = onLongPress
                    )
                }

                else -> {
                    Text(
                        text = "ERROR!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = messageModifier
                    )
                }
            }

        }
    }
}

@Composable
fun MessageCreationRow(
    modifier: Modifier = Modifier,
    audioRecorder: AudioRecorder,
    onAudioRecorderFinished: (String?) -> Unit,
    onAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: (String) -> Unit
) {
    val context = LocalContext.current
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

            AnimatedIconButton(
                onActionDown = {
                    audioRecorder.startRecording { path ->
                        getPrivateStorageFileFromFilePath(
                            context,
                            path
                        )
                    }
                },
                onActionUp = {
                    onAudioRecorderFinished(audioRecorder.stopRecording())
                    ClickSoundManager.playClickSound()
                }
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Record")
            }

            IconButton(onClick = onAttachmentClick) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach")
                ClickSoundManager.playClickSound()
            }

            IconButton(onClick = onCameraClick) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                ClickSoundManager.playClickSound()
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .testTag(TestTags.NOTE_CONTENT_FIELD),
                placeholder = { Text("Aa") },
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendClick(text)
                        text = ""
                        ClickSoundManager.playClickSound()
                    }
                },
                modifier = Modifier
                    .bounceClick()
                    .testTag(TestTags.SAVE_MESSAGE_BUTTON)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onTitleChanged: (String) -> Unit,
    onNavigationClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = title)) }

    TopAppBar(
        title = {
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onTitleChanged(it.text)
                },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.NOTE_TITLE_FIELD),
                decorationBox = { innerTextField ->
                    if (textFieldValue.text.isBlank()) {
                        Text(stringResource(id = R.string.title), color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationClick,
                modifier = Modifier.testTag(TestTags.BACK_FROM_CHAT_NOTES)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                ClickSoundManager.playClickSound()
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
                ClickSoundManager.playClickSound()
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {

                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.delete)) },
                    onClick = {
                        onDelete()
                        showMenu = false
                    }
                )
            }
        }
    )
}


@Composable
fun DeleteDialog(
    isShown: Boolean,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Dialog(
        isShown = isShown,
        title = stringResource(id = R.string.delete_confirmation),
        text = stringResource(id = R.string.are_you_sure_you_want_to_delete),
        onConfirm = onConfirmDelete,
        onDismiss = onDismissDelete
    )
}
