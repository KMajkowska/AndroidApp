package com.example.androidapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun TextPreview(data: String, placeholder: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable { onEditClick() }
    ) {
        if (data.isBlank()){
            Text(
                text=placeholder,
                style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier.wrapContentSize()
            )
        }
        else {

            Text(
                text = data,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}

@Composable
fun TextEditorWithPreview(
    data: String,
    placeholder: String,
    textEditor: @Composable (possibleNewData: String, onCloseEditor: (possiblyEditedData: String) -> Unit) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        textEditor(data) {
            isEditing = false
        }
    } else {
        TextPreview(data = data, placeholder = placeholder) {
            isEditing = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InlineTextEditor(
    data: String,
    hasDayEntityBeenChanged: MutableState<Boolean>,
    onCloseEditor: (possiblyChangedData: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var tempText by remember { mutableStateOf(data) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        keyboardController?.show()
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    onCloseEditor(data)
                }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
            }

            Button(
                onClick = {
                    if (tempText.isNotBlank()) {
                        hasDayEntityBeenChanged.value = true
                        keyboardController?.hide()
                        val stringWithoutEnters = tempText.replace("\n", " ")
                        onCloseEditor(stringWithoutEnters)
                    } else {
                        onCloseEditor("")
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
            }
        }

        TextField(
            value = tempText,
            onValueChange = { tempText = it },
            modifier = Modifier.fillMaxSize()
                .testTag(TestTags.INLINE_TEXT_EDITOR_FIELD)
                .focusRequester(focusRequester)
        )
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogTextEditor(
    data: String,
    hasDayEntityBeenChanged: MutableState<Boolean>,
    onCloseEditor: (possiblyChangedData: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var tempText by remember { mutableStateOf(data) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {

        Dialog(
            onDismissRequest = {
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = tempText,
                    onValueChange = {
                        tempText = it
                    },
                    label = {
                        Text("Content")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            onCloseEditor(data)
                            showDialog = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                    }

                    Button(
                        onClick = {
                            hasDayEntityBeenChanged.value = true
                            keyboardController?.hide()
                            onCloseEditor(tempText)
                            showDialog = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                    }
                }
            }
        }
    }
}