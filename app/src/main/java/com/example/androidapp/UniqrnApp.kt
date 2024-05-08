package com.example.androidapp

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import java.time.LocalDate

var defaultPicture = R.drawable.pen
@Composable
fun UniqrnApp() {
    val navController = rememberNavHostController()
    val current = LocalContext.current
    LaunchedEffect(key1 = true) {
        val mediaPlayer = MediaPlayer.create(current, R.raw.music)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        delay(3000)
    }

    val mSettingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
    )

    val selectedLanguage by mSettingsViewModel.selectedLanguage.observeAsState(LanguageEnum.ENGLISH)
    val isDarkTheme by mSettingsViewModel.isDarkTheme.observeAsState(true)
    val isUniqrnTheme by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(true)
    val areNotificationsEnabled by mSettingsViewModel.areNotificationsEnabled.observeAsState(true)

    defaultPicture = if (isUniqrnTheme){
         R.drawable.uniqrn_app
    }
    else {
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
                unqirnNavGraph(
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
                unqirnNavGraph(
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

private fun NavGraphBuilder.unqirnNavGraph(
    mSettingsViewModel: SettingsViewModel,
    mDayViewModel: DayViewModel,
    onDaySelected: (LocalDate, NavBackStackEntry) -> Unit,
    onNoteSelected: (Long, NavBackStackEntry) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSettingsClick: () -> Unit,
    upPress: () -> Unit
) {
    val localDateConverter = LocalDateConverter()
    val tabs = listOf(NavItem.ALL_NOTES, NavItem.CALENDAR, NavItem.DAYS)

    val noteId = "noteId"
    val localDate = "localDate"

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
                {onSettingsClick()},
                { noteId -> onNoteSelected(noteId, backStackEntry) }
            ) { date -> onDaySelected(date, backStackEntry) }, onNavigateToRoute
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
        route = "${ScreenRoutes.DAYS}?$localDate={$localDate}",
        arguments = listOf(navArgument(localDate) {
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
                getDateFromStringOrNow(arguments.getString(localDate), LocalDate.now())!!
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
            ).ViewWithBackground()
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
        route = "${ScreenRoutes.CHAT_NOTES}?$noteId={$noteId}",
        arguments = listOf(
            navArgument(noteId) {
                defaultValue = -1
                type = NavType.LongType
            }
        )) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val noteForeignId = arguments.getLong(noteId, -1)
        ChatNotes(
            noteForeignId = noteForeignId,
            mDayViewModel = mDayViewModel,
            upPress = upPress
        ).ViewWithBackground()
    }
}
