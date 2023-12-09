package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapp.Dialog
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.Toggle
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

class SettingsScreen: NavigableScreen() {

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
