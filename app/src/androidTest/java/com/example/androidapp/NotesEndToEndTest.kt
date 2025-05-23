package com.example.androidapp

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotesEndToEndTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

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
            .performTextInput("TEST TITLE 1")
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_CONTENT_FIELD)
            .performTextInput("TEST CONTENT 1")

        composeTestRule
            .onNodeWithContentDescription("Send")
            .performClick()

        composeTestRule
            .onNodeWithTag(TestTags.BACK_FROM_CHAT_NOTES)
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText("TEST TITLE 1")
            .assertExists()
        //composeTestRule
        //    .onNodeWithText("TEST CONTENT 1")
        //    .assertExists()
    }

    @Test
    fun testAppNavigationBetweenNotesAndCalendarAndDays() {
        val context = composeTestRule.activity.applicationContext
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithText(context.getString(R.string.days))
            .assertHasClickAction()
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.ALL_NOTES_VIEW)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag(TestTags.DAYS_SCREEN_VIEW)
            .assertExists()

        composeTestRule
            .onNodeWithText(context.getString(R.string.no_note))
            .assertHasClickAction()
            .performClick()
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_EDITOR_VIEW)
            .assertExists()
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_TITLE_FIELD)
            .performTextInput("TEST TITLE ")
        composeTestRule
            .onNodeWithTag(TestTags.NOTE_CONTENT_FIELD)
            .performTextInput("TEST CONTENT")

        composeTestRule
            .onNodeWithTag(TestTags.SAVE_MESSAGE_BUTTON)
            .assertExists()
            .assertHasClickAction()
            //.performClick()

        composeTestRule
            .onNodeWithTag(TestTags.BACK_FROM_CHAT_NOTES)
            .assertExists()
            .assertHasClickAction()
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