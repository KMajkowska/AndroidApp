package com.example.androidapp.navigablescreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

class CalendarScreen : NavigableScreen() {

    override val screenName: String
        get() = "Calendar"

    override fun screenIcon(): ImageVector {
        return Icons.Default.ShoppingCart
    }

    @Composable
    override fun View() {
        Text(text = screenName)
    }

}