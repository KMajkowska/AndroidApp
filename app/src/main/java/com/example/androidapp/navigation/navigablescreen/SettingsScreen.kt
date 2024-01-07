package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.R
import com.example.androidapp.Toggle
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.settings.FontSizeEnum
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.SettingsViewModel

val languages = LanguageEnum.entries.toTypedArray()
val fontSizes = FontSizeEnum.entries.toTypedArray()
val sortOptions = NoteSortOptionEnum.entries.toTypedArray()

class SettingsScreen(
    //private val mDayViewModel: DayViewModel,
    private val navigateToFilePicker: (String) -> Unit,
    private val settingsViewModel: SettingsViewModel
) : NavigableScreen() {
    @Composable
    override fun View() {
        val selectedLanguage by settingsViewModel.selectedLanguage.observeAsState(LanguageEnum.ENGLISH)
        val notificationsEnabled by settingsViewModel.areNotificationsEnabled.observeAsState(false)
        val isDarkModeEnabled by settingsViewModel.isDarkTheme.observeAsState(false)
        val selectedFontSize by settingsViewModel.selectedFontSize.observeAsState(FontSizeEnum.STANDARD)
        val unicornModeEnabled by settingsViewModel.isUniqrnModeEnabled.observeAsState(false)
        val selectedSortOption by settingsViewModel.selectedSortOption.observeAsState(NoteSortOptionEnum.ASCENDING)

        val context = LocalContext.current

        Surface {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                item {
                    DropDown(
                        dropdownName = stringResource(id = R.string.language),
                        allOptions = languages,
                        valueFromOptionGetterFunction = { language -> language.toString()},
                        selectedValueModifier = selectedLanguage
                    ) { newLanguage ->
                        settingsViewModel.setSelectedLanguage(newLanguage)
                    }

                    HorizontalDivider()
                    Toggle(
                        toggleOption = notificationsEnabled,
                        text = stringResource(id = R.string.notification)
                    ) { areNotificationsEnabled ->
                        settingsViewModel.setNotificationsEnabled(areNotificationsEnabled)
                    }

                    HorizontalDivider()
                    Toggle(
                        toggleOption = isDarkModeEnabled,
                        text = stringResource(id = R.string.dark_mode),
                    ) { isDarkModeEnabled ->
                        settingsViewModel.setDarkTheme(isDarkModeEnabled)
                    }

                    HorizontalDivider()
                    DropDown(
                        dropdownName = stringResource(id = R.string.font_size),
                        allOptions = fontSizes,
                        valueFromOptionGetterFunction = { fontSize -> context.resources.getString(fontSize.resourceId)},
                        selectedValueModifier = selectedFontSize
                    ) { fontSize ->
                        settingsViewModel.setSelectedFontSize(fontSize)
                    }

                    HorizontalDivider()
                    DropDown(
                        dropdownName = stringResource(id = R.string.sort_option),
                        allOptions = sortOptions,
                        valueFromOptionGetterFunction = { sortOption -> context.resources.getString(sortOption.resourceId)},
                        selectedValueModifier = selectedSortOption
                    ) { sortOption ->
                        settingsViewModel.setSeletectedSortOption(sortOption)
                    }

                    HorizontalDivider()
                    Toggle(
                        toggleOption = unicornModeEnabled,
                        text = stringResource(id = R.string.unicorn)
                    ) { isUniqrnModeEnabled ->
                        settingsViewModel.setUniqrnMode(isUniqrnModeEnabled)
                    }

                    HorizontalDivider()
                    BackupImportDialog(navigateToFilePicker)
                }
            }
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

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(id = R.string.choose_action)) },
            text = { Text(stringResource(id = R.string.backup_or_import)) },
            confirmButton = {
                TextButton(onClick = onBackup) { Text(stringResource(id = R.string.backup)) }
            },
            dismissButton = {
                TextButton(onClick = onImport) { Text(stringResource(id = R.string.import_string)) }
            }
        )
    }

    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            title = { Text(stringResource(id = R.string.confirmation)) },
            text = { Text(stringResource(id = R.string.continue_confirmation)) },
            confirmButton = {
                TextButton(onClick = confirmAction) { Text(stringResource(id = R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmationDialog.value = false
                }) { Text(stringResource(id = R.string.no)) }
            }
        )
    }

    Button(onClick = { showDialog.value = true }) {
        Text(
            "${stringResource(id = R.string.backup)}/${stringResource(id = R.string.import_string)} ${
                stringResource(
                    id = R.string.data
                )
            }"
        )
    }
}
