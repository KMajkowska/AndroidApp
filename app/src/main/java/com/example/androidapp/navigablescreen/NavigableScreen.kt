package com.example.androidapp.navigablescreen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.androidapp.AddBackgroundToComposables

abstract class NavigableScreen : ComponentActivity() {
    @Composable
    open fun ViewWithBackground() {
        AddBackgroundToComposables({ View() })
    }

    abstract val screenName: String

    abstract fun screenIcon(): ImageVector

    @Composable
    protected abstract fun View()

}
