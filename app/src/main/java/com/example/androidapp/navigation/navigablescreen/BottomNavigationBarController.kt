package com.example.androidapp.navigation.navigablescreen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.database.viewmodel.DayViewModelFactory
import com.example.androidapp.ui.theme.AndroidAppTheme
/*
@Composable
fun CustomBottomNavigation() {

    val context = LocalContext.current
    val mDayViewModel: DayViewModel = viewModel(
        factory = DayViewModelFactory(context.applicationContext as Application)
    )
    /*
     We could use metaprogramming to find all these elements
     but doing this that way makes sure that the ordering
     on the navigation bar is correct
     */
    val screens: List<NavigableScreen> = listOf(
        AllNotes(mDayViewModel, localDateConverter.toLocalDate(arguments.getString(dateString))),
        CalendarScreen(
            mDayViewModel,
            localDateConverter.toLocalDate(arguments.getString(dateString)!!)
        ),
        DaysScreen(mDayViewModel, localDateConverter.toLocalDate(arguments.getString(dateString)!!)),
        SettingsScreen()
    )
    var selectedScreen by remember { mutableStateOf(screens.first()) }

    Scaffold(bottomBar = {
        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.screenIcon(),
                            contentDescription = screen.screenNameAndRoute,
                        )
                    },
                    label = { Text(screen.screenNameAndRoute) },
                    selected = screen == selectedScreen,
                    onClick = { selectedScreen = screen },
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
            selectedScreen.ViewWithBackground()
        }
    })
}
 */

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarControllerPreview() {
    AndroidAppTheme {
        //CustomBottomNavigation()
    }
}
