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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.Year
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

        val groupedByYear =
            allDayEntitiesWithRelatedSortedByDate.groupBy { dayEntityWithRelated ->
                Year.from(
                    dayEntityWithRelated.dayEntity.date
                )
            }
        var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
        val lazyListState = rememberLazyListState()

        Surface {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                state = lazyListState
            ) {
                items(groupedByYear.entries.toList()) { (year, objects) ->
                    YearSquare(year, objects, currentYearMonth, onDaySelected)
                }
        }

        }
    }

@Composable
fun YearSquare(
    year: Year,
    objects: List<DayWithTodosAndEvents>,
    currentYearMonth: YearMonth,
    onDaySelected: (LocalDate) -> Unit
) {
    var isYearExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Text(
            text = year.toString(),
            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
                .clickable { isYearExpanded = !isYearExpanded }
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isYearExpanded) {
            val groupedByYearMonth =
                objects.groupBy { dayEntityWithRelated ->
                    YearMonth.from(
                        dayEntityWithRelated.dayEntity.date
                    )
                }
            groupedByYearMonth.forEach { (yearMonth, days) ->
                MonthSquare(yearMonth, days, onDaySelected)
            }
        }
    }
}
    @Composable
    fun MonthSquare(
        yearMonth: YearMonth,
        objects: List<DayWithTodosAndEvents>,
        onDaySelected: (LocalDate) -> Unit
    ) {
        var isMonthExpanded by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = yearMonth.month.toString(),
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { isMonthExpanded = !isMonthExpanded }
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                
            )

            if(isMonthExpanded) {
                objects.forEach { obj ->
                    ObjectItem(obj, onDaySelected)
                }
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
                .clickable { onDaySelected(obj.dayEntity.date) }
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${obj.dayEntity.date.dayOfMonth},  ${obj.dayEntity.dayTitle}",
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(10.dp, 4.dp, 10.dp, 4.dp))
            Spacer(modifier = Modifier.height(4.dp))
            //display events planned for the day
            Column {
                obj.events.forEach { event ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        val icon = getCategoryIcon(event.category)
                        Icon(imageVector = icon, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = event.title,
                            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}