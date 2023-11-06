package com.example.androidapp.navigablescreen

import android.widget.CalendarView
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidapp.AddBackgroundToComposables

class DaysScreen : NavigableScreen() {

    override val screenName: String
        get() = "Days"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Create
    }

    @Composable
    override fun ViewWithBackground() {
        AddBackgroundToComposables({ Calendar() }, { View() })
    }

    @Composable
    override fun View() {
        Text(text = screenName)
    }

    @Composable
    fun Calendar() {
        Row(
            modifier = Modifier
                .padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {

            AndroidView(
                { CalendarView(it) }, // this 'it' is the current context!
                modifier = Modifier.wrapContentWidth(),
                update = { // TODO: implement}
                }
            )
        }
    }

}