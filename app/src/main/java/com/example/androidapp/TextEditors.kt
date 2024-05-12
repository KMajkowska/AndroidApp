package com.example.androidapp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
        if (data.isBlank()) {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.wrapContentSize()
            )
        } else {

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
        textEditor(data) { isEditing = false }
    } else {
        TextPreview(data = data, placeholder = placeholder) { isEditing = true }
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
            modifier = Modifier
                .fillMaxSize()
                .testTag(TestTags.INLINE_TEXT_EDITOR_FIELD)
                .focusRequester(focusRequester)
        )
    }
}

@Composable
fun TextEditorDialog(
    showDialogTextEditor: Boolean,
    onDismiss: () -> Unit,
    onSave: (text: String) -> Unit
) {
    var textState by remember { mutableStateOf("") }

    if (showDialogTextEditor) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(stringResource(id = R.string.edit_text), style = MaterialTheme.typography.bodyLarge)

                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.onSecondaryContainer,
                                MaterialTheme.shapes.small
                            )
                    )

                    Row(modifier = Modifier.padding(top = 24.dp)) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(width = 100.dp, height = 48.dp)
                        ) {
                            Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.headlineMedium)
                        }

                        Button(
                            onClick = {
                                if (textState.isNotBlank()) {
                                    onSave(textState)
                                }
                            },
                            modifier = Modifier
                                .size(width = 100.dp, height = 48.dp)
                        ) {
                            Text(stringResource(id = R.string.save), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
