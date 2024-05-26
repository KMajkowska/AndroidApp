package com.example.androidapp

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WholeAppLanguageTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    @get:Rule
    val composeTestRuleWholeApp = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: SettingsRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        repository = mockk(relaxed = true)

        viewModel = SettingsViewModel(repository)
    }


    @Test
    fun testEnglishWholeAppLanguage(){
        composeTestRuleWholeApp.activity.setContent {
            UniqrnAppCustomSettings(viewModel, LanguageEnum.ENGLISH)
        }
        // days
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

        // go back to all notes to go to settings
        composeTestRuleWholeApp
            .onNodeWithText("All notes")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("All notes")
            .performClick()

        // settings
        composeTestRuleWholeApp
            .onNodeWithTag(TestTags.SETTINGS_SCREEN_NAVIGATE_BUTTON)
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithTag(TestTags.SETTINGS_SCREEN_NAVIGATE_BUTTON)
            .performClick()
        composeTestRuleWholeApp
            .onNodeWithText("Language")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Notification")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Sort option")
            .assertIsDisplayed()
    }

    @Test
    fun testPolishWholeAppLanguage(){
        composeTestRuleWholeApp.activity.setContent {
            UniqrnAppCustomSettings(viewModel, LanguageEnum.POLSKI)
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

        // go back to all notes to go to settings
        composeTestRuleWholeApp
            .onNodeWithText("Notatki")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Notatki")
            .performClick()

        composeTestRuleWholeApp
            .onNodeWithTag(TestTags.SETTINGS_SCREEN_NAVIGATE_BUTTON)
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithTag(TestTags.SETTINGS_SCREEN_NAVIGATE_BUTTON)
            .performClick()
        composeTestRuleWholeApp
            .onNodeWithText("Język")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Powiadomienie")
            .assertIsDisplayed()
        composeTestRuleWholeApp
            .onNodeWithText("Opcja sortowania")
            .assertIsDisplayed()
    }
}