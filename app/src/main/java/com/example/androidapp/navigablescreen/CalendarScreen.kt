package com.example.androidapp.navigablescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        val chosenDate = remember { mutableStateOf(LocalDate.now()) }
        AddBackgroundToComposables({ View(chosenDate) })
    }

    @Composable
    override fun View() {
        TODO("Not yet implemented")
    }


    @Composable
    fun View(chosenDate: MutableState<LocalDate>) {
        // Get all DayEntities and EventEntities
        val allDayEntities =
            mDayViewModel.allDayEntitiesSortedByDate.observeAsState(initial = listOf()).value
        val allEventEntities =
            mDayViewModel.allEventEntities.observeAsState(initial = listOf()).value

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display each DayEntity and its associated EventEntities
            items(allDayEntities) { dayEntity ->
                // Display Year
                this@LazyColumn.item {
                    Text(
                        text = dayEntity.date.year.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Display Month
                this@LazyColumn.item {
                    Text(
                        text = DateTimeFormatter.ofPattern("MMMM").format(dayEntity.date),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Get EventEntities associated with the current DayEntity
                val eventsForDay = allEventEntities.filter { it.dayForeignId == dayEntity.dayId }

                // Display Events for the current DayEntity
                this@LazyColumn.items(eventsForDay) { event ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // Display Day and Event Title
                        Text(
                            text = "${dayEntity.date.dayOfMonth} ${event.title}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}