package com.example.androidapp.navigablescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TextPreview(data: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable { onEditClick() }
    ) {
        Text(
            text = data,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun TextEditorWithPreview(
    data: String,
    textEditor: @Composable (possibleNewData: String, onCloseEditor: (possiblyEditedData: String) -> Unit) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        textEditor(data) {
            isEditing = false
        }
    } else {
        TextPreview(data = data) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = tempText,
            onValueChange = { tempText = it },
            modifier = Modifier.fillMaxSize()
        )

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
                    hasDayEntityBeenChanged.value = true
                    keyboardController?.hide()
                    onCloseEditor(tempText)
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
            }
        }
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