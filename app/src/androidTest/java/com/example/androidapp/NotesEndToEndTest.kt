package com.example.androidapp

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.androidapp.navigation.navigablescreen.DaysScreen
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotesEndToEndTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp(){
        composeTestRule.activity.setContent {
            UniqrnApp()
        }
    }

    @Test
    fun testCreatingAndSavingANoteProperly(){
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithContentDescription("Create note")
            .performClick()
        composeTestRule
           .onNodeWithTag(TestTags.NOTE_EDITOR_VIEW)
           .assertExists()
        composeTestRule
            .onNodeWithText(TestTags.ALL_NOTES_VIEW)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_TITLE_FIELD)
            .performTextInput("TEST TITLE")
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_CONTENT_FIELD)
            .performTextInput("TEST CONTENT")
        composeTestRule
            .onNodeWithContentDescription("Save")
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithTag(TestTags.DISPLAYED_NOTE_TITLE, useUnmergedTree = true)
            .assertTextEquals("TEST TITLE")
        composeTestRule
            .onNodeWithTag(TestTags.DISPLAYED_NOTE_CONTENT, useUnmergedTree = true)
            .assertTextEquals("TEST CONTENT")
    }

    @Test
    fun testAppNavigationBetweenNotesAndCalendarAndDays(){
        val context = composeTestRule.activity.applicationContext
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText(context.getString(R.string.days))
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(TestTags.DAYS_SCREEN_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText(context.getString(R.string.no_note))
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_EDITOR_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_TITLE_FIELD)
            .performTextInput("TEST TITLE")
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_CONTENT_FIELD)
            .performTextInput("TEST CONTENT")
        composeTestRule
            .onNodeWithContentDescription("Save")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("Add new event")
            .assertHasClickAction()
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.INLINE_TEXT_EDITOR_FIELD)
            .performTextInput("TEST EVENT")
        composeTestRule
            .onNodeWithContentDescription("Save")
            .performScrollTo()
        composeTestRule
            .onNodeWithContentDescription("Save")
            .assertHasClickAction()
            .performClick()
        composeTestRule
            .onNodeWithText("TEST EVENT")
            .assertExists()
        composeTestRule
            .onNodeWithTag(TestTags.DAYS_SCREEN_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText(context.getString(R.string.all_notes))
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("Redirect to days")
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.DAYS_SCREEN_VIEW)
            .assertExists()

        composeTestRule
            .onNodeWithText(context.getString(R.string.calendar))
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.CALENDAR_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText("TEST EVENT")
            .assertExists()
            .assertHasClickAction()
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.DAYS_SCREEN_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithTag(TestTags.CALENDAR_VIEW)
            .assertDoesNotExist()
    }
}