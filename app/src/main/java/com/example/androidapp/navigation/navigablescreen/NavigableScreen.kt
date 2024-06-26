package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.androidapp.AddBackgroundToComposables

// component activity inheritance is unnecessary! It makes these untestable
abstract class NavigableScreen/* : ComponentActivity()*/ {

    @Composable
    open fun ViewWithBackground(padding: PaddingValues = PaddingValues(4.dp)) {
        AddBackgroundToComposables(padding = padding, { View() })
    }

    @Composable
    protected abstract fun View()
}
