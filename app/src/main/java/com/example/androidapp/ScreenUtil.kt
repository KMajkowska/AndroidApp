package com.example.androidapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddBackgroundToComposables(vararg composables: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.hsv(242F, 0.729F, 1F), Color.hsv(209F, 0.683F, 0.953F)),
                    startY = 0.0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            composables.forEach { composable ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        composable()
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalDivider() {
    Spacer(modifier = Modifier.height(8.dp))
    Divider(modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun Dialog(
    text: String,
    functionCall: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text)
            },
            confirmButton = {
                Button(
                    onClick = {
                        functionCall()
                        showDialog.value = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        )
    }

    Button(
        onClick = {
            showDialog.value = true
        }
    ) {
        Text(text)
    }
}

@Composable
fun Toggle(
    toggleOption: MutableState<Boolean>,
    text: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text)
            Switch(
                checked = toggleOption.value,
                onCheckedChange = { toggleOption.value = it })
        }
    }
}

@Composable
fun <T> DropDown(
    dropdownName: String,
    allOptions: Array<T>,
    selectedValueModifierFunction: MutableState<T>
) {
    // Language Dropdown
    Column(
        verticalArrangement = Arrangement.Center, // Align children vertically at the center
        horizontalAlignment = Alignment.Start, // Align children horizontally at the center
        modifier = Modifier.fillMaxWidth() // Fill the available width
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Align children vertically at the center
            horizontalArrangement = Arrangement.spacedBy(4.dp) // Add spacing between children
        ) {
            var expanded by remember { mutableStateOf(false) }
            Text(text = dropdownName)
            Column {
                TextButton(onClick = { expanded = true }) {
                    Text(
                        text = selectedValueModifierFunction.value.toString(),
                        color = Color.Black
                    )
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Localized description"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.toString()) },
                            onClick = {
                                selectedValueModifierFunction.value = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}