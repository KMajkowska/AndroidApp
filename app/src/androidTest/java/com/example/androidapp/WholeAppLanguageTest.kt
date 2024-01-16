package com.example.androidapp

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WholeAppLanguageTest {

    @get:Rule
    val composeTestRuleWholeApp = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        repository = mockk(relaxed = true)

        viewModel = SettingsViewModel(repository)
    }


    @Test
    fun testEnglishWholeAppLanguage(){
        viewModel.setSelectedLanguage(LanguageEnum.ENGLISH)
        composeTestRuleWholeApp.activity.setContent {
            UniqrnAppSettings(viewModel, LanguageEnum.ENGLISH)
        }
        composeTestRuleWholeApp
            .onNodeWithText("Days")
            .performClick()
        composeTestRuleWholeApp
            .onNodeWithText("All notes")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Days")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("To do")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Events List")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Calendar")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Settings")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Language")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Notifications")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Sort Option")
            .assertIsDisplayed()


    }

    @Test
    fun testPolishWholeAppLanguage(){
        composeTestRuleWholeApp.activity.setContent {
            UniqrnAppSettings(viewModel, LanguageEnum.POLSKI)
        }
        composeTestRuleWholeApp
            .onNodeWithText("Dni")
            .performClick()
        composeTestRuleWholeApp
            .onNodeWithText("Notatki")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Dni")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Do zrobienia")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Lista wydarzeń")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Kalendarz")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Ustawienia")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Język")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Powiadomienia")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Sortowanie")
            .assertIsDisplayed()
    }
}