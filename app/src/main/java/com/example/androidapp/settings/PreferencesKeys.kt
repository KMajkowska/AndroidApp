package com.example.androidapp.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeysNames {
    val SELECTED_LANGUAGE_NAME = "selected_language"
    val NOTIFICATIONS_ENABLED_NAME = "are_notifications_enabled"
    val IS_DARK_THEME_NAME = "is_dark_theme"
    val IS_UNIQRN_MODE_ENABLED_NAME = "is_uniqrn_mode_enabled"
    val SELECTED_SORT_OPTION_NAME = "selected_sort_option"
}

object PreferencesKeys {
    val SELECTED_LANGUAGE = stringPreferencesKey(PreferenceKeysNames.SELECTED_LANGUAGE_NAME)
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey(PreferenceKeysNames.NOTIFICATIONS_ENABLED_NAME)
    val IS_DARK_THEME = booleanPreferencesKey(PreferenceKeysNames.IS_DARK_THEME_NAME)
    val IS_UNIQRN_MODE_ENABLED = booleanPreferencesKey(PreferenceKeysNames.IS_UNIQRN_MODE_ENABLED_NAME)
    val SELECTED_SORT_OPTION = stringPreferencesKey(PreferenceKeysNames.SELECTED_SORT_OPTION_NAME)
}