package com.example.androidapp.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.database.model.Note
import com.example.androidapp.navigation.navigablescreen.NavigableScreen
import java.time.LocalDate

object ScreenRoutes {
    const val ALL_NOTES = "allNotes"
    const val CALENDAR = "calendar"
    const val DAYS = "days"
    const val SETTINGS = "settings"
    const val CREATE_NOTE = "createNote"
}

enum class NavItem(
    /*@StringRes val title: Int*/ val title: String,
    val icon: ImageVector,
    val route: String
) {
    ALL_NOTES(/*R.string.home_feed*/ "All notes", Icons.Default.AddCircle, ScreenRoutes.ALL_NOTES),
    CALENDAR("Calendar", Icons.Default.CalendarMonth, ScreenRoutes.CALENDAR),
    DAYS("Days", Icons.Default.AddAlarm, ScreenRoutes.DAYS),
    SETTINGS("Settings", Icons.Outlined.Settings, ScreenRoutes.SETTINGS),
}

@Composable
fun CustomBottomNavigation(
    tabs: List<NavItem>,
    currentRoute: String,
    currentScreen: NavigableScreen,
    navigateToRoute: (String) -> Unit,
) {
    val currentSection = tabs.first { it.route == currentRoute }

    Scaffold(bottomBar = {
        NavigationBar {
            tabs.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                        )
                    },
                    label = { Text(tab.title) },
                    selected = tab == currentSection,
                    onClick = { navigateToRoute(tab.route) },
                )
            }
        }
    }, content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            currentScreen.ViewWithBackground()
        }
    })
}

@Composable
fun rememberNavHostController(
    navController: NavHostController = rememberNavController()
): BottomNavBarController = remember(navController) {
    BottomNavBarController(navController)
}

@Stable
class BottomNavBarController(val navController: NavHostController) {
    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    private val localDateConverter = LocalDateConverter()

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
//                popUpTo(findStartDestination(navController.graph).id) {
//                    saveState = true
//                }
            }
        }
    }

    fun navigateToDayDetail(localDate: LocalDate, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed())
            navController.navigate("${ScreenRoutes.DAYS}?localDate=${localDateConverter.fromLocalDate(localDate)}")
    }

    fun navigateToNoteEditor(noteId: Long, localDate: LocalDate?, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed())
            navController.navigate(
                "${ScreenRoutes.CREATE_NOTE}?noteId=$noteId" +
                        if (localDate == null) "" else "&localDate=${localDateConverter.fromLocalDate(localDate)}"
            )

    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.getLifecycle().currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}