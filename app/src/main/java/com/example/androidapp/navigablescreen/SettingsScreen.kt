package com.example.androidapp.navigablescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.androidapp.settings.FontSizeEnum
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.StyleModeEnum
import com.example.androidapp.settings.SupportedFontEnum

// All these will be a language based enum (a map of enums maybe)
val languages = LanguageEnum.values()
val modes = StyleModeEnum.values()
val fonts = SupportedFontEnum.values()
val fontSizes = FontSizeEnum.values()
val sortOptions = NoteSortOptionEnum.values()

class SettingsScreen : NavigableScreen() {
    override val screenName: String
        get() = "Settings"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Settings
    }

    // TODO: Add actual states of the application, make selecting actually call functions
    @Composable
    override fun View() {
        val selectedLanguage = remember { mutableStateOf(languages.first()) }
        val notificationsEnabled = remember { mutableStateOf(true) }
        val selectedMode = remember { mutableStateOf(modes.first()) }
        val selectedFont = remember { mutableStateOf(fonts.first()) }
        val selectedFontSize = remember { mutableStateOf(fontSizes.first()) }
        val unicornModeEnabled = remember { mutableStateOf(false) }
        val selectedSortOption = remember { mutableStateOf(sortOptions.first()) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            DropDown(
                dropdownName = "Language",
                allOptions = languages,
                selectedValueModifierFunction = selectedLanguage
            )

            HorizontalDivider()
            Toggle(toggleOption = notificationsEnabled, text = "Notifications")

            HorizontalDivider()
            DropDown(
                dropdownName = "Modes",
                allOptions = modes,
                selectedValueModifierFunction = selectedMode
            )

            HorizontalDivider()
            DropDown(
                dropdownName = "Font",
                allOptions = fonts,
                selectedValueModifierFunction = selectedFont
            )

            HorizontalDivider()
            DropDown(
                dropdownName = "Font size",
                allOptions = fontSizes,
                selectedValueModifierFunction = selectedFontSize
            )

            HorizontalDivider()
            DropDown(
                dropdownName = "Sort option",
                allOptions = sortOptions,
                selectedValueModifierFunction = selectedSortOption
            )

            HorizontalDivider()
            Toggle(toggleOption = unicornModeEnabled, text = "Unicorn mode")

            HorizontalDivider()
            Dialog(text = "Backup", functionCall = { })

            HorizontalDivider()
            Dialog(text = "Sign out", functionCall = { })

            HorizontalDivider()
            Dialog(text = "Delete account", functionCall = { })
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
                        // Handle confirmation logic here
                        functionCall()
                        showDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Cancel")
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
