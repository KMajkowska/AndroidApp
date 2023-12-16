package com.example.androidapp.navigation.navigablescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.YearMonth

class CalendarScreen(
    private val mDayViewModel: DayViewModel,
    private val localDate: LocalDate,
    private val onDaySelected: (LocalDate) -> Unit,
) : NavigableScreen() {

    @Composable
    override fun View() {

        val allDayEntitiesWithRelatedSortedByDate =
            mDayViewModel.allDayEntitiesWithRelatedSortedByDate.observeAsState(initial = listOf()).value

        val groupedByYearMonth =
            allDayEntitiesWithRelatedSortedByDate.groupBy { dayEntityWithRelated ->
                YearMonth.from(
                    dayEntityWithRelated.dayEntity.date
                )
            }

        var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
        val lazyListState = rememberLazyListState()

        LaunchedEffect(currentYearMonth) {
            val index = groupedByYearMonth.keys.indexOf(currentYearMonth)
            if (index >= 0)
                lazyListState.scrollToItem(index)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            state = lazyListState
        ) {
            items(groupedByYearMonth.entries.toList()) { (yearMonth, objects) ->
                YearSquare(yearMonth, objects, currentYearMonth, onDaySelected)
            }
        }
    }
}

@Composable
fun YearSquare(
    yearMonth: YearMonth,
    objects: List<DayWithTodosAndEvents>,
    currentYearMonth: YearMonth,
    onDaySelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = yearMonth.year.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        MonthSquare(yearMonth, objects, currentYearMonth, onDaySelected)
    }
}

@Composable
fun MonthSquare(
    yearMonth: YearMonth,
    objects: List<DayWithTodosAndEvents>,
    currentYearMonth: YearMonth,
    onDaySelected: (LocalDate) -> Unit
) {
    val isCurrentYearMonth = yearMonth == currentYearMonth

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color.hsv(227F, 0.939F, 0.961F, 0.22F))
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = yearMonth.month.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        objects.forEach { obj ->
            ObjectItem(obj, onDaySelected)
        }
    }
}

@Composable
fun ObjectItem(obj: DayWithTodosAndEvents, onDaySelected: (LocalDate) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onDaySelected(obj.dayEntity.date) }
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${obj.dayEntity.date.dayOfMonth},  ${obj.dayEntity.dayTitle}")
        Spacer(modifier = Modifier.height(4.dp))
        //display events planned for the day
        Column {
            obj.events.forEach { event ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    val icon = getCategoryIcon(event.category)
                    Icon(imageVector = icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(event.title)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}