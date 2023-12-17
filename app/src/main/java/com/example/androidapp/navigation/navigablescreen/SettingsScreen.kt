package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidapp.Dialog
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.R
import com.example.androidapp.Toggle
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.FontSizeEnum
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.StyleModeEnum
import com.example.androidapp.settings.SupportedFontEnum


val languages = LanguageEnum.values()
val modes = StyleModeEnum.values()
val fonts = SupportedFontEnum.values()
val fontSizes = FontSizeEnum.values()
val sortOptions = NoteSortOptionEnum.values()

class SettingsScreen(private val mDayViewModel : DayViewModel): NavigableScreen() {

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            item {
                DropDown(
                    dropdownName = stringResource(id = R.string.language),
                    allOptions = languages,
                    selectedValueModifierFunction = selectedLanguage
                )

                Button(onClick = {
                    mDayViewModel.setLanguage(selectedLanguage.value.code)
                }) {
                    Text(stringResource(id = R.string.apply_language))
                }

                HorizontalDivider()
                Toggle(toggleOption = notificationsEnabled, text = stringResource(id = R.string.notification))

                HorizontalDivider()
                DropDown(
                    dropdownName = stringResource(id = R.string.modes),
                    allOptions = modes,
                    selectedValueModifierFunction = selectedMode
                )


                Button(onClick = {
                    if(selectedMode.value.name == "DARK")
                        mDayViewModel.toggleTheme(true)
                    else
                        mDayViewModel.toggleTheme(false)
                }) {
                    Text(stringResource(id = R.string.change_mode))
                }


                HorizontalDivider()
                DropDown(
                    dropdownName = stringResource(id = R.string.font),
                    allOptions = fonts,
                    selectedValueModifierFunction = selectedFont
                )

                HorizontalDivider()
                DropDown(
                    dropdownName = stringResource(id = R.string.font_size),
                    allOptions = fontSizes,
                    selectedValueModifierFunction = selectedFontSize
                )

                HorizontalDivider()
                DropDown(
                    dropdownName = stringResource(id = R.string.sort_option),
                    allOptions = sortOptions,
                    selectedValueModifierFunction = selectedSortOption
                )

                HorizontalDivider()
                Toggle(toggleOption = unicornModeEnabled, text = stringResource(id = R.string.unicorn))

                HorizontalDivider()
                Dialog(text = stringResource(id = R.string.backup), functionCall = { })

                HorizontalDivider()
                Dialog(text = stringResource(id = R.string.sign_out), functionCall = { })

                HorizontalDivider()
                Dialog(text = stringResource(id = R.string.delete_account), functionCall = { })
            }
        }

    }
}
