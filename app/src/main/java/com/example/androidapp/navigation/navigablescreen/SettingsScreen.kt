package com.example.androidapp.navigation.navigablescreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidapp.Dialog
import com.example.androidapp.DropDown
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.R
import com.example.androidapp.Toggle
import com.example.androidapp.settings.FontSizeEnum
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.StyleModeEnum
import com.example.androidapp.settings.SupportedFontEnum
import java.util.Locale


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

        val context = LocalContext.current
        val sharedPreferences = remember(context) { PreferenceManager.getDefaultSharedPreferences(context) }

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
                    setLocaleLang(selectedLanguage.value.code, context, sharedPreferences)
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

    private fun setLocaleLang(lang: String, context: Context, sharedPreferences: SharedPreferences) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val editor = sharedPreferences.edit()
        editor.putString("My_Lang", lang)
        editor.apply()

        val intent = Intent("com.example.androidapp.LANGUAGE_CHANGED")
        context.sendBroadcast(intent)
    }

}
