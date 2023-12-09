package com.example.androidapp.navigation.navigablescreen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.example.androidapp.AddBackgroundToComposables

abstract class NavigableScreen() : ComponentActivity() {

    @Composable
    open fun ViewWithBackground() {
        AddBackgroundToComposables({ View() })
    }

    @Composable
    protected abstract fun View()
}
