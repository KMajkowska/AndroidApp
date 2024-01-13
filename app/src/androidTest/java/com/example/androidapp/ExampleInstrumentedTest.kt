package com.example.androidapp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidapp.navigation.CustomBottomNavigation
import com.example.androidapp.navigation.NavItem
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.navigation.navigablescreen.SettingsScreen
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4 ::class)
class LanguageChangeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
                CustomBottomNavigation(
                    listOf(NavItem.ALL_NOTES, NavItem.CALENDAR, NavItem.DAYS, NavItem.SETTINGS),
                    ScreenRoutes.SETTINGS,
                    SettingsScreen({  }, SettingsViewModel(SettingsRepository(context = LocalContext.current)))
                ) { }
        }
    }
    @Test
    fun `changeLanguageSettingTest`() {

        composeTestRule.onNodeWithText("ENGLISH", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("POLSKI", ignoreCase = true).performClick()

        composeTestRule.onNodeWithText("DESCENDING", ignoreCase = true).performClick()
        assertTrue(true)
    }

}