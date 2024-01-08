package com.example.androidapp.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val selectedLanguage = repository.selectedLanguage.asLiveData()
    val isDarkTheme = repository.isDarkTheme.asLiveData()
    val isUniqrnModeEnabled = repository.isUniqrnModeEnabled.asLiveData()
    val areNotificationsEnabled = repository.areNotificationsEnabled.asLiveData()
    val selectedSortOption = repository.selectedSortOption.asLiveData()

    fun setSelectedLanguage(language: LanguageEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSelectedLanguage(language)
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setDarkTheme(isDark)
        }
    }

    fun setUniqrnMode(isUniqrnModeEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUniqrnMode(isUniqrnModeEnabled)
        }
    }

    fun setNotificationsEnabled(areNotificationsEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setNotificationsEnabled(areNotificationsEnabled)
        }
    }

    fun setSeletectedSortOption(selectedSortOption: NoteSortOptionEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSelectedSortOption(selectedSortOption)
        }
    }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}