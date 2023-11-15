package com.example.androidapp.navigablescreen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CalendarScreen(private val mDayViewModel: DayViewModel) : NavigableScreen() {
    override val screenName: String
        get() = "Calendar"

    override fun screenIcon(): ImageVector {
        return Icons.Default.CalendarMonth
    }

    @Composable
    override fun ViewWithBackground() {
        AddBackgroundToComposables({ View() })
    }

    @Composable
    override fun View() {

        val navController = rememberNavController()

        val events = mapOf(
            LocalDate.now().minusYears(1) to listOf("Event A", "Event B"),
            LocalDate.now() to listOf("Event 1", "Event 2"),
            LocalDate.now().plusMonths(1) to listOf("Event 3", "Event 4"),
            LocalDate.now().plusYears(1) to listOf("Event 5", "Event 6"),
            LocalDate.now().plusYears(2) to listOf("Event X", "Event Y")
        )

        LazyColumn {
            items(events.entries.toList()) { (date, monthEvents) ->
                CalendarMonth(date, monthEvents)
            }
        }
    }

    @Composable
    fun CalendarMonth(date: LocalDate, events: List<String>) {

        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = date.year.toString(),
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )

            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMMM")),
                modifier = Modifier
                    .padding(bottom = 8.dp)
//                    .clickable {
//                        val intent = Intent(context, DaysScreen::class.java)
//                        context.startActivity(intent)
//                    }
            )

            if (events.isNotEmpty()) {
                Text(
                    text = "Potential Events:",
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                events.forEach { event ->
                    Text(
                        text = " - $event",
                        modifier = Modifier
                            .padding(start = 16.dp)
//                            .clickable {
//                                val intent = Intent(context, DaysScreen::class.java)
//                                context.startActivity(intent)
//                            }
                    )
                }
            }

            val nextMonthEvents = emptyList<String>()
            if (nextMonthEvents.isNotEmpty()) {
                nextMonthEvents.forEach { event ->
                    Text(
                        text = " - $event",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            //TODO clicking crashes the app
//                            .clickable {
//                                val intent = Intent(context, DaysScreen::class.java)
//                                context.startActivity(intent)
//                            }
                    )
                }
            }

            val nextYearMonthEvents = emptyList<String>()
            if (nextYearMonthEvents.isNotEmpty()) {
                nextYearMonthEvents.forEach { event ->
                    Text(
                        text = " - $event",
                        modifier = Modifier
                            .padding(start = 16.dp)
//                            .clickable {
//                                val intent = Intent(context, DaysScreen::class.java)
//                                context.startActivity(intent)
//                            }
                    )
                }
            }
        }
    }
}
