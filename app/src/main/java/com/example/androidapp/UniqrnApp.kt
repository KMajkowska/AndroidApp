package com.example.androidapp

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            upPress = navController::upPress,
            onNavigateToRoute = navController::navigateToBottomBarRoute,
            mDayViewModel = mDayViewModel
        )
    }
}

private fun NavGraphBuilder.unqirnNavGraph(
    mDayViewModel: DayViewModel,
    onDaySelected: (LocalDate, NavBackStackEntry) -> Unit,
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    val localDateConverter = LocalDateConverter()
    val dateString = "dateString"
    val tabs = listOf(NavItem.ALL_NOTES, NavItem.CALENDAR, NavItem.DAYS, NavItem.SETTINGS)

    composable(
        route = ScreenRoutes.ALL_NOTES
    ) { _ ->
        CustomBottomNavigation(
            tabs = tabs,
            currentRoute = ScreenRoutes.ALL_NOTES,
            currentScreen = AllNotes(
                mDayViewModel,
                LocalDate.now()
            ),
            navigateToRoute = onNavigateToRoute
        )
    }

    composable(
        route = ScreenRoutes.CALENDAR
    ) { _ ->
        CustomBottomNavigation(
            tabs = tabs,
            currentRoute = ScreenRoutes.CALENDAR,
            currentScreen = CalendarScreen(
                mDayViewModel,
                LocalDate.now(),
                onDaySelected
            ),
            navigateToRoute = onNavigateToRoute
        )
    }

    composable(
        route = ScreenRoutes.DAYS,
        arguments = listOf(navArgument(dateString) {
            type = NavType.StringType
        })
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        CustomBottomNavigation(
            tabs = tabs,
            currentRoute = ScreenRoutes.DAYS,
            currentScreen = DaysScreen(
                mDayViewModel,
                localDateConverter.toLocalDate(arguments.getString(dateString)!!)
            ),
            navigateToRoute = onNavigateToRoute
        )
    }
    composable(
        route = ScreenRoutes.SETTINGS
    ) { _ ->
        CustomBottomNavigation(
            tabs = tabs,
            currentRoute = ScreenRoutes.SETTINGS,
            currentScreen = SettingsScreen(),
            navigateToRoute = onNavigateToRoute
        )
    }

}