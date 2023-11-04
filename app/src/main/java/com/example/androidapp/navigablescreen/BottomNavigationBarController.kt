package com.example.androidapp.navigablescreen

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidapp.ui.theme.AndroidAppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomBottomNavigation() {
    /*
     We could use metaprogramming to find all these elements
     but doing this that way makes sure that the ordering
     on the navigation bar is correct
     */
    val screens: List<NavigableScreen> = listOf(AllNotes(), CreateNoteScreen(), SettingsScreen())
    var selectedScreen by remember { mutableStateOf(screens.first()) }

    Scaffold(bottomBar = {
        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(icon = {
                    Icon(
                        imageVector = screen.screenIcon(),
                        contentDescription = screen.screenName
                    )
                },
                    label = { Text(screen.screenName) },
                    selected = screen == selectedScreen,
                    onClick = { selectedScreen = screen },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }, content = {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            selectedScreen.View()
        }

    })
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarControllerPreview() {
    AndroidAppTheme {
        CustomBottomNavigation()
    }
}