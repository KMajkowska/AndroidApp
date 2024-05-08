package com.example.androidapp.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.R
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.navigation.navigablescreen.NavigableScreen
import java.time.LocalDate

object ScreenRoutes {
    const val ALL_NOTES = "allNotes"
    const val CALENDAR = "calendar"
    const val DAYS = "days"
    const val SETTINGS = "settings"
    const val CHAT_NOTES = "chatNotes"
    const val IMPORT_PICKER = "importPicker"
    const val BACKUP_PICKER = "backupPicker"
}

enum class NavItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    ALL_NOTES(R.string.all_notes, Icons.Default.AddCircle, ScreenRoutes.ALL_NOTES),
    CALENDAR(R.string.calendar, Icons.Default.CalendarMonth, ScreenRoutes.CALENDAR),
    DAYS(R.string.days, Icons.Default.AddAlarm, ScreenRoutes.DAYS),
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
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(id = tab.title),
                            tint = if (tab == currentSection) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = tab.title),
                            color = if (tab == currentSection) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    selected = tab == currentSection,
                    onClick = { navigateToRoute(tab.route) },
                    modifier = Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                navigateToRoute(tab.route)
                            }
                        }
                        .background(
                            MaterialTheme.colorScheme.primary// Set the background color for the selected item)
                        ))
            }
        }
    }, content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
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
    private val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    private val localDateConverter = LocalDateConverter()

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute)
            navController.navigate(route)
    }

    fun navigateToDayDetail(localDate: LocalDate, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed())
            navController.navigate(
                "${ScreenRoutes.DAYS}?localDate=${
                    localDateConverter.fromLocalDate(
                        localDate
                    )
                }"
            )
    }

    fun navigateToDayChatNotes(noteId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed())
            navController.navigate(
                "${ScreenRoutes.CHAT_NOTES}?noteId=$noteId"
            )
    }

    fun navigateToSettings() {
        navController.navigate(ScreenRoutes.SETTINGS)
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.getLifecycle().currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}