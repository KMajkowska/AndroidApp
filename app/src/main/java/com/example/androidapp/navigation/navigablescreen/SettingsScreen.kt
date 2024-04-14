package com.example.androidapp.navigation.navigablescreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SoundOptions

val languages = LanguageEnum.entries.toTypedArray()
val sortOptions = NoteSortOptionEnum.entries.toTypedArray()
val soundOptions = SoundOptions.entries.toTypedArray()

class SettingsScreen(
    private val navigateToFilePicker: (String) -> Unit,
    private val settingsViewModel: SettingsViewModel
) : NavigableScreen() {
    @Composable
    override fun View() {
        val selectedLanguage by settingsViewModel.selectedLanguage.observeAsState(LanguageEnum.ENGLISH)
        val notificationsEnabled by settingsViewModel.areNotificationsEnabled.observeAsState(false)
        val isDarkModeEnabled by settingsViewModel.isDarkTheme.observeAsState(false)
        val unicornModeEnabled by settingsViewModel.isUniqrnModeEnabled.observeAsState(false)
        val selectedSortOption by settingsViewModel.selectedSortOption.observeAsState(NoteSortOptionEnum.ASCENDING)
        val selectedSoundOption by settingsViewModel.selectedSoundOption.observeAsState(SoundOptions.NONE)

        val context = LocalContext.current

        Surface(modifier = Modifier.testTag(TestTags.SETTINGS_SCREEN_VIEW)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
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
                    MyToggle(
                        toggleOption = notificationsEnabled,
                        text = stringResource(id = R.string.notification)
                    ) { areNotificationsEnabled ->
                        settingsViewModel.setNotificationsEnabled(areNotificationsEnabled)
                    }

                    HorizontalDivider()
                    MyToggle(
                        toggleOption = isDarkModeEnabled,
                        text = stringResource(id = R.string.dark_mode),
                    ) { isDarkModeEnabled ->
                        settingsViewModel.setDarkTheme(isDarkModeEnabled)
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
                    MyToggle(
                        toggleOption = unicornModeEnabled,
                        text = stringResource(id = R.string.unicorn)
                    ) { isUniqrnModeEnabled ->
                        settingsViewModel.setUniqrnMode(isUniqrnModeEnabled)
                    }

                    HorizontalDivider()
                    BackupImportDialog(navigateToFilePicker)

                    HorizontalDivider()
                    DropDown(
                        dropdownName = stringResource(id = R.string.sound),
                        allOptions = soundOptions,
                        valueFromOptionGetterFunction = { soundOption -> context.resources.getString(soundOption.resourceId)},
                        selectedValueModifier = selectedSoundOption
                    ) { soundOption ->
                        settingsViewModel.setSelectedSound(soundOption)
                    }

                    HorizontalDivider()
                    val image = painterResource(R.drawable.uniqrn_app)
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
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


@Composable
fun MyToggle(toggleOption: Boolean, text: String, onToggle: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(0.dp, 12.dp, 0.dp, 8.dp)
                    .fillMaxWidth()
            )
            Switch(
                checked = toggleOption,
                onCheckedChange = { onToggle(!toggleOption) },
            )
        }
    }
}
