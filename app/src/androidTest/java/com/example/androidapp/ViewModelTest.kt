package com.example.androidapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4 ::class)
class ViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        // Set the main dispatcher to test dispatcher
        Dispatchers.setMain(Dispatchers.Unconfined)

        // Initialize a mock repository
        repository = mockk(relaxed = true)

        // Initialize the ViewModel with the mock repository
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher to the original dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun setSelectedLanguageUpdatesLanguageInRepository() = runBlocking {
        // Given
        val testLanguage = LanguageEnum.POLSKI

        // When
        viewModel.setSelectedLanguage(testLanguage)

        // Then
        coVerify { repository.setSelectedLanguage(testLanguage) }
    }

    @Test
    fun setNoteSortedOptionsUpdatesSortingInRepository() = runBlocking {
        // Given
        val testSorted = NoteSortOptionEnum.DESCENDING

        // When
        viewModel.setSeletectedSortOption(testSorted)

        // Then
        coVerify { repository.setSelectedSortOption(testSorted) }
    }

    @Test
    fun setToDarkModeUpdatesModeInRepository() = runBlocking {
        // Given
        val testMode = true

        // When
        viewModel.setDarkTheme(testMode)

        // Then
        coVerify { repository.setDarkTheme(testMode) }
    }

    @Test
    fun setUniqrnModeUpdatesModeInRepository() = runBlocking {
        // Given
        val testMode = true

        // When
        viewModel.setUniqrnMode(testMode)

        // Then
        coVerify { repository.setUniqrnMode(testMode) }
    }

    @Test
    fun setNotificationEnabledUpdatesNotificationInRepository() = runBlocking {
        // Given
        val testNotifications = true

        // When
        viewModel.setNotificationsEnabled(testNotifications)

        // Then
        coVerify { repository.setNotificationsEnabled(testNotifications) }
    }


}