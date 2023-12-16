package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.Toggle
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.settings.FontSizeEnum
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.StyleModeEnum
import com.example.androidapp.settings.SupportedFontEnum

// All these will be a language based enum (a map of enums maybe)
val languages = LanguageEnum.entries.toTypedArray()
val modes = StyleModeEnum.entries.toTypedArray()
val fonts = SupportedFontEnum.entries.toTypedArray()
val fontSizes = FontSizeEnum.entries.toTypedArray()
val sortOptions = NoteSortOptionEnum.entries.toTypedArray()

class SettingsScreen(private val navigateToFilePicker: (String) -> Unit): NavigableScreen() {

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
            BackupImportDialog(navigateToFilePicker)

        }
    }
}

@Composable
fun BackupImportDialog(navigateTo: (String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val isBackup = remember { mutableStateOf(true) }

    val onBackup = {
        isBackup.value = true
        showConfirmationDialog.value = true
    }

    val onImport = {
        isBackup.value = false
        showConfirmationDialog.value = true
    }

    val confirmAction = {
        navigateTo(if (isBackup.value) ScreenRoutes.BACKUP_PICKER else ScreenRoutes.IMPORT_PICKER)
        showConfirmationDialog.value = false
    }

    // Backup and Import options dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Choose Action") },
            text = { Text("Would you like to backup or import the database?") },
            confirmButton = {
                TextButton(onClick = onBackup) { Text("Backup") }
            },
            dismissButton = {
                TextButton(onClick = onImport) { Text("Import") }
            }
        )
    }

    // Confirmation dialog
    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to ${if (isBackup.value) "backup" else "import"} the database?") },
            confirmButton = {
                TextButton(onClick = confirmAction) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog.value = false }) { Text("No") }
            }
        )
    }

    // Button to show the options dialog
    Button(onClick = { showDialog.value = true }) {
        Text("Backup/Import Database")
    }
}
