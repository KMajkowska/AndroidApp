package com.example.androidapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SettingsViewModelTests {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        repository = mockk(relaxed = true)

        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setSelectedLanguageUpdatesLanguageInRepository() = runBlocking {
        val testLanguage = LanguageEnum.POLSKI

        viewModel.setSelectedLanguage(testLanguage)

        coVerify { repository.setSelectedLanguage(testLanguage) }
    }

    @Test
    fun setNoteSortedOptionsUpdatesSortingInRepository() = runBlocking {
        val testSorted = NoteSortOptionEnum.DESCENDING

        viewModel.setSeletectedSortOption(testSorted)

        coVerify { repository.setSelectedSortOption(testSorted) }
    }

    @Test
    fun setToDarkModeUpdatesModeInRepository() = runBlocking {
        val testMode = true

        viewModel.setDarkTheme(testMode)

        coVerify { repository.setDarkTheme(testMode) }
    }

    @Test
    fun setUniqrnModeUpdatesModeInRepository() = runBlocking {
        val testMode = true

        viewModel.setUniqrnMode(testMode)

        coVerify { repository.setUniqrnMode(testMode) }
    }

    @Test
    fun setNotificationEnabledUpdatesNotificationInRepository() = runBlocking {
        val testNotifications = true

        viewModel.setNotificationsEnabled(testNotifications)

        coVerify { repository.setNotificationsEnabled(testNotifications) }
    }


}