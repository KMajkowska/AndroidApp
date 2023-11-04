package com.example.androidapp.navigablescreen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.Language

// All these will be a language based enum (a map of enums maybe)
val languages = listOf("English", "Polski")
val modes = listOf("Light mode", "Dark mode", "Device mode")
val fonts = listOf("Arial", "Comic Sans")
val fontSizes = listOf("Medium", "Small", "Big")

class SettingsScreen() : NavigableScreen() {
    override val screenName: String
        get() = "Settings"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Settings
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun View() {
        val context = LocalContext.current

        var selectedLanguage by remember { mutableStateOf(languages.first()) }
        var notificationsEnabled by remember { mutableStateOf(true) }
        var selectedMode by remember { mutableStateOf(modes.first()) }
        var selectedFont by remember { mutableStateOf(fonts.first()) }
        var selectedFontSize by remember { mutableStateOf(fontSizes.first()) }
        var unicornModeEnabled by remember { mutableStateOf(false) }
        var selectedSortOption by remember { mutableStateOf("Date Created") }

        var showDialog by remember { mutableStateOf(false) }

        Scaffold(content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Language Dropdown
                DropDown(
                    dropdownName = "Language",
                    allOptions = languages,
                    selectedValueModifierFunction = { l -> selectedLanguage = l }
                )

                // Notifications Toggle
                HorizontalDivider()
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Notifications")
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it })
                    }
                }

                // Mode Dropdown
                HorizontalDivider()
                DropDown(
                    dropdownName = "Modes",
                    allOptions = modes,
                    selectedValueModifierFunction = { m -> selectedMode = m }
                )

                // Add more settings options here...

                // Dialog buttons
                HorizontalDivider()
                Column {
                    Button(onClick = { showDialog = true }) {
                        Text("Backup")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { showDialog = true }) {
                        Text("Sign Out")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { showDialog = true }) {
                        Text("Delete Account")
                    }
                }
            }
        })

        // Dialog to be shown on button click
        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false },
                title = { Text("Confirmation") },
                text = { Text("Are you sure you want to perform this action?") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        // Handle button click action here
                        Toast.makeText(context, "Action performed", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                })
        }

    }
}

@Composable
fun HorizontalDivider() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun <T> DropDown(
    dropdownName: String,
    allOptions: List<T>,
    selectedValueModifierFunction: (T) -> Unit
) {
    // Language Dropdown
    Column {

        Box {
            var expanded by remember { mutableStateOf(false) }
            Text(text = dropdownName)
            IconButton(onClick = { expanded = true }) {
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
                            selectedValueModifierFunction(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

}
