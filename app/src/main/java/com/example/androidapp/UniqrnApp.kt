package com.example.androidapp

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.database.viewmodel.DayViewModelFactory
import com.example.androidapp.navigation.CustomBottomNavigation
import com.example.androidapp.navigation.NavItem
import com.example.androidapp.navigation.ScreenRoutes
import com.example.androidapp.navigation.navigablescreen.AllNotes
import com.example.androidapp.navigation.navigablescreen.CalendarScreen
import com.example.androidapp.navigation.navigablescreen.CreateNote
import com.example.androidapp.navigation.navigablescreen.DaysScreen
import com.example.androidapp.navigation.navigablescreen.SettingsScreen
import com.example.androidapp.navigation.rememberNavHostController
import java.time.LocalDate

@Composable
fun UniqrnApp() {
    val navController = rememberNavHostController()
    val mDayViewModel: DayViewModel = viewModel(
        factory = DayViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    NavHost(
        navController = navController.navController,
        startDestination = ScreenRoutes.ALL_NOTES
    ) {
        unqirnNavGraph(
            onDaySelected = navController::navigateToDayDetail,
            onNoteSelected = navController::navigateToNoteEditor,
            upPress = navController::upPress,
            onNavigateToRoute = navController::navigateToBottomBarRoute,
            mDayViewModel = mDayViewModel
        )
    }
}

private fun NavGraphBuilder.unqirnNavGraph(
    mDayViewModel: DayViewModel,
    onDaySelected: (LocalDate, NavBackStackEntry) -> Unit,
    onNoteSelected: (Long, LocalDate?, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    val localDateConverter = LocalDateConverter()
    val tabs = listOf(NavItem.ALL_NOTES, NavItem.CALENDAR, NavItem.DAYS, NavItem.SETTINGS)

    //args
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
                LocalDate.now()
            ) { noteId -> onNoteSelected(noteId, null, backStackEntry) },
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
            ) { noteId, date ->
                onNoteSelected(
                    noteId,
                    date,
                    backStackEntry
                )
            },
            onNavigateToRoute
        )
    }

    composable(route = ScreenRoutes.SETTINGS) { _ ->
        CustomBottomNavigation(
            tabs,
            ScreenRoutes.SETTINGS,
            SettingsScreen(),
            onNavigateToRoute
        )
    }

    composable(
        route = "${ScreenRoutes.CREATE_NOTE}?$noteId={$noteId}&$localDate={$localDate}",
        arguments = listOf(
            navArgument(noteId) {
                defaultValue = -1
                type = NavType.LongType
            },
            navArgument(localDate) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val noteIdArgument = arguments.getLong("noteId", -1)
        CreateNote(
            mDayViewModel,
            noteIdArgument,
            getDateFromStringOrNow(arguments.getString(localDate), null),
            upPress
        )
            .ViewWithBackground()
    }
}


