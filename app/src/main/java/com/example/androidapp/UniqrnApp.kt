package com.example.androidapp

import android.app.Application
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.database.viewmodel.DayViewModelFactory
import com.example.androidapp.navigation.CustomBottomNavigation
import com.example.androidapp.navigation.NavItem
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.navigation.navigablescreen.AllNotes
import com.example.androidapp.navigation.navigablescreen.CalendarScreen
import com.example.androidapp.navigation.navigablescreen.ChatNotes
import com.example.androidapp.navigation.navigablescreen.DaysScreen
import com.example.androidapp.navigation.navigablescreen.FilePicker
import com.example.androidapp.navigation.navigablescreen.SettingsScreen
import com.example.androidapp.navigation.rememberNavHostController
import com.example.androidapp.notifications.NotificationHelper
import com.example.androidapp.settings.LanguageEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SettingsViewModelFactory
import com.example.androidapp.ui.theme.AndroidAppTheme
import com.example.androidapp.ui.theme.LanguageAwareScreen
import java.time.LocalDate

const val NOTE_ID_QUERY_STRING_PARAM = "noteId"
const val LOCAL_DATE_QUERY_STRING_PARAM = "localDate"

val tabs = listOf(NavItem.ALL_NOTES, NavItem.CALENDAR, NavItem.DAYS)
val fullScreenPaddingValues = PaddingValues(
    top = 32.dp,
    bottom = 32.dp,
    start = 4.dp,
    end = 4.dp
)

var defaultPicture = R.drawable.pen


@Composable
fun UniqrnApp() {
    val navController = rememberNavHostController()

    val mSettingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
    )

    val selectedLanguage by mSettingsViewModel.selectedLanguage.observeAsState(LanguageEnum.ENGLISH)
    val isDarkTheme by mSettingsViewModel.isDarkTheme.observeAsState(true)
    val isUniqrnTheme by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(true)
    val areNotificationsEnabled by mSettingsViewModel.areNotificationsEnabled.observeAsState(true)

    defaultPicture =
        if (isUniqrnTheme) {
            R.drawable.uniqrn_app
        } else {
            R.drawable.pen
        }
    val notificationHelper = NotificationHelper(LocalContext.current, areNotificationsEnabled)
    val mDayViewModel: DayViewModel = viewModel(
        factory = DayViewModelFactory(
            LocalContext.current.applicationContext as Application,
            notificationHelper
        )
    )

    AndroidAppTheme(isDarkTheme, isUniqrnTheme) {
        LanguageAwareScreen(selectedLanguage.code) {
            NavHost(
                navController = navController.navController,
                startDestination = ScreenRoutes.ALL_NOTES
            ) {
                uniqrnNavGraph(
                    onDaySelected = navController::navigateToDayDetail,
                    onNoteSelected = navController::navigateToDayChatNotes,
                    upPress = navController::upPress,
                    onNavigateToRoute = navController::navigateToBottomBarRoute,
                    mDayViewModel = mDayViewModel,
                    mSettingsViewModel = mSettingsViewModel,
                    onSettingsClick = navController::navigateToSettings,
                )
            }
        }
    }
}

@Composable
fun UniqrnAppSettings(
    mSettingsViewModel: SettingsViewModel,
    setting: LanguageEnum
) {
    val navController = rememberNavHostController()

    val selectedLanguage by mSettingsViewModel.selectedLanguage.observeAsState(setting)
    val isDarkTheme by mSettingsViewModel.isDarkTheme.observeAsState(true)
    val isUniqrnTheme by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(true)
    val areNotificationsEnabled by mSettingsViewModel.areNotificationsEnabled.observeAsState(true)

    val notificationHelper = NotificationHelper(LocalContext.current, areNotificationsEnabled)
    val mDayViewModel: DayViewModel = viewModel(
        factory = DayViewModelFactory(
            LocalContext.current.applicationContext as Application,
            notificationHelper
        )
    )

    AndroidAppTheme(isDarkTheme, isUniqrnTheme) {
        LanguageAwareScreen(selectedLanguage.code) {
            NavHost(
                navController = navController.navController,
                startDestination = ScreenRoutes.ALL_NOTES
            ) {
                uniqrnNavGraph(
                    onDaySelected = navController::navigateToDayDetail,
                    upPress = navController::upPress,
                    onNavigateToRoute = navController::navigateToBottomBarRoute,
                    onNoteSelected = navController::navigateToDayChatNotes,
                    mDayViewModel = mDayViewModel,
                    mSettingsViewModel = mSettingsViewModel,
                    onSettingsClick = navController::navigateToSettings
                )
            }
        }
    }
}

private fun NavGraphBuilder.uniqrnNavGraph(
    mSettingsViewModel: SettingsViewModel,
    mDayViewModel: DayViewModel,
    onDaySelected: (LocalDate, NavBackStackEntry) -> Unit,
    onNoteSelected: (Long, NavBackStackEntry) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSettingsClick: () -> Unit,
    upPress: () -> Unit
) {
    val localDateConverter = LocalDateConverter()

    fun getDateFromStringOrNow(dateString: String?, default: LocalDate?): LocalDate? {
        return if (dateString == null) default else localDateConverter.toLocalDate(dateString)
    }

    composable(route = ScreenRoutes.ALL_NOTES) { backStackEntry ->
        CustomBottomNavigation(
            tabs,
            ScreenRoutes.ALL_NOTES,
            AllNotes(
                mDayViewModel,
                LocalDate.now(),
                { onSettingsClick() },
                { noteId -> onNoteSelected(noteId, backStackEntry) }
            ) { date -> onDaySelected(date, backStackEntry) },
            onNavigateToRoute
        )
    }

    composable(route = ScreenRoutes.CALENDAR) { backStackEntry ->
        CustomBottomNavigation(
            tabs,
            ScreenRoutes.CALENDAR,
            CalendarScreen(
                mDayViewModel,
                LocalDate.now()
            ) { date -> onDaySelected(date, backStackEntry) },
            onNavigateToRoute
        )
    }

    composable(
        route = "${ScreenRoutes.DAYS}?$LOCAL_DATE_QUERY_STRING_PARAM={$LOCAL_DATE_QUERY_STRING_PARAM}",
        arguments = listOf(navArgument(LOCAL_DATE_QUERY_STRING_PARAM) {
            nullable = true
            defaultValue = null
            type = NavType.StringType
        })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        CustomBottomNavigation(
            tabs,
            ScreenRoutes.DAYS,
            DaysScreen(
                mDayViewModel,
                getDateFromStringOrNow(
                    arguments.getString(LOCAL_DATE_QUERY_STRING_PARAM),
                    LocalDate.now()
                )!!
            ) { noteId ->
                onNoteSelected(
                    noteId,
                    backStackEntry
                )
            },
            onNavigateToRoute
        )
    }

    composable(
        route = ScreenRoutes.SETTINGS
    ) { _ ->
        SettingsScreen(
            onNavigateToRoute,
            mSettingsViewModel,
            upPress
        ).ViewWithBackground(fullScreenPaddingValues)
    }

    composable(route = ScreenRoutes.IMPORT_PICKER) { _ ->
        FilePicker(
            mDayViewModel = mDayViewModel,
            upPress = upPress,
            isExport = false
        ).ViewWithBackground()
    }

    composable(route = ScreenRoutes.BACKUP_PICKER) { _ ->
        FilePicker(
            mDayViewModel = mDayViewModel,
            upPress = upPress,
            isExport = true
        ).ViewWithBackground()
    }

    composable(
        route = "${ScreenRoutes.CHAT_NOTES}?$NOTE_ID_QUERY_STRING_PARAM={$NOTE_ID_QUERY_STRING_PARAM}",
        arguments = listOf(
            navArgument(NOTE_ID_QUERY_STRING_PARAM) {
                defaultValue = -1
                type = NavType.LongType
            }
        )) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val noteForeignId = arguments.getLong(NOTE_ID_QUERY_STRING_PARAM, -1)
        ChatNotes(
            noteForeignId = noteForeignId,
            mDayViewModel = mDayViewModel,
            upPress = upPress
        ).ViewWithBackground(fullScreenPaddingValues)
    }
}
