package com.example.androidapp.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")
class SettingsRepository(private val context: Context) {
    val selectedLanguage: Flow<LanguageEnum> = context.dataStore.data
        .map { preferences ->
            LanguageEnum.valueOf((preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: LanguageEnum.ENGLISH).toString())
        }

    suspend fun setSelectedLanguage(language: LanguageEnum) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language.toString()
        }
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] ?: false
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false
        }

    suspend fun setNotificationsEnabled(areNotificationsEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = areNotificationsEnabled
        }
    }

    val isUniqrnModeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_UNIQRN_MODE_ENABLED] ?: false
        }

    suspend fun setUniqrnMode(isUniqrnModeEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_UNIQRN_MODE_ENABLED] = isUniqrnModeEnabled
        }
    }
    val selectedFontSize: Flow<FontSizeEnum> = context.dataStore.data
        .map { preferences ->
            FontSizeEnum.valueOf(preferences[PreferencesKeys.SELECTED_FONT_SIZE] ?: FontSizeEnum.STANDARD.toString())
        }

    suspend fun setSelectedFontSize(fontSize: FontSizeEnum) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_FONT_SIZE] = fontSize.toString()
        }
    }
    val selectedSortOption: Flow<NoteSortOptionEnum> = context.dataStore.data
        .map { preferences ->
            NoteSortOptionEnum.valueOf(preferences[PreferencesKeys.SELECTED_SORT_OPTION] ?: NoteSortOptionEnum.DESCENDING.toString())
        }

    suspend fun setSelectedSortOption(selectedSortOption: NoteSortOptionEnum) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_SORT_OPTION] = selectedSortOption.toString()
        }
    }
}