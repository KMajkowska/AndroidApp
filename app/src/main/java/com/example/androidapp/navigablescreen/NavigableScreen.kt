package com.example.androidapp.navigablescreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.androidapp.ui.theme.AndroidAppTheme

abstract class NavigableScreen : ComponentActivity() {
    @Composable
    abstract fun View(): @Composable Unit
    abstract val screenName: String
    abstract fun screenIcon(): ImageVector
/*
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                AndroidAppTheme {
                    View()
                }
            }
        }

     */
}