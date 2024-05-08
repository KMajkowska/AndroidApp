package com.example.androidapp.navigation.navigablescreen

import android.annotation.SuppressLint
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SettingsViewModelFactory
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale


class CalendarScreen(
    private val mDayViewModel: DayViewModel,
    private val localDate: LocalDate,
    private val onDaySelected: (LocalDate) -> Unit,
) : NavigableScreen() {

    @SuppressLint("UnrememberedMutableState")
    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun View() {
        var dayEntity by remember { mutableStateOf(mDayViewModel.getDayByDate(localDate)) }
        var selectedNote by remember { mutableStateOf(mDayViewModel.getNoteByDate(localDate)) }

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
        var isCalendarExpanded = mutableStateOf(false)
        val currentMonthYear = remember { mutableStateOf(YearMonth.now()) }
        val monthYearText = currentMonthYear.value.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthYearText
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            isCalendarExpanded.value = !isCalendarExpanded.value
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isCalendarExpanded.value) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = if (isCalendarExpanded.value) "Collapse" else "Expand"
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isCalendarExpanded.value,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize()
                ) {
                    CalendarView(
                        expanded = isCalendarExpanded,
                        dayEntity,
                        onDaySelected
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .animateContentSize(animationSpec = tween(100)),
                    state = lazyListState
                ) {
                    items(groupedByYear.entries.toList()) { (year, objects) ->
                        YearSquare(year, objects, currentYearMonth, onDaySelected)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    fun CalendarView(
        expanded: MutableState<Boolean>,
        day: DayEntity,
        onDaySelected: (LocalDate) -> Unit,
    ) {
        val mSettingsViewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
        )
        val isDarkMode by mSettingsViewModel.isDarkTheme.observeAsState(false)
        val isUniqrnMode by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(false)

        AnimatedVisibility(
            visible = expanded.value,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    AndroidView(
                        factory = { context ->
                            val themedContext = ContextThemeWrapper(
                                context,
                                if (isUniqrnMode && isDarkMode) R.style.CalendarTextAppearance_DarkUnicorn
                                else if (isDarkMode) R.style.CalendarTextAppearance_Dark
                                else if (isUniqrnMode) R.style.CalendarTextAppearance_Unicorn
                                else R.style.CalendarTextAppearance_Light
                            )
                            CalendarView(themedContext).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                )
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    onDaySelected(selectedDate)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { calendarView ->
                            val calendar = Calendar.getInstance()
                            val chosenDateInMillis: Long = calendar.apply {
                                set(Calendar.YEAR, day.date.year)
                                set(Calendar.MONTH, day.date.monthValue - 1)
                                set(Calendar.DAY_OF_MONTH, day.date.dayOfMonth)
                            }.timeInMillis

                            // Initially select the current date in the calendar
                            calendarView.setDate(chosenDateInMillis)
                        }
                    )
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
            if(isYearExpanded){
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

        val gradientColors = remember { generateGradientColors(objects) }
        val colors = remember { generateColors(objects)}
        val seasonColor = gradientColors.getValue(yearMonth)
        val color = colors.getValue(yearMonth)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                //.background(MaterialTheme.colorScheme.primaryContainer),
                .background(seasonColor),
        ) {

            val seasonIcon = when (yearMonth.month) {
                Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Icons.Filled.AcUnit
                Month.MARCH, Month.APRIL, Month.MAY -> Icons.Outlined.LocalFlorist
                Month.JUNE, Month.JULY, Month.AUGUST -> Icons.Outlined.WbSunny
                Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Icons.Outlined.Eco
                else -> Icons.Filled.QuestionMark
            }
            Row(modifier = Modifier
                .background(color)
            ){
                Icon(
                    imageVector = seasonIcon,
                    contentDescription = "Season Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    text = yearMonth.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        LocalContext.current.resources.configuration.locale
                    ).uppercase(),
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { isMonthExpanded = !isMonthExpanded }
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,

                    )

            }

            if (isMonthExpanded) {
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
                .testTag(TestTags.DAY_IN_CALENDAR)
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

    private fun generateColors(objects: List<DayWithTodosAndEvents>): Map<YearMonth, Color> {
        return objects.map { dayWithTodosAndEvents ->
            val yearMonth = YearMonth.from(dayWithTodosAndEvents.dayEntity.date)
            val secondColor = when (yearMonth.month) {
                Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Color(0xFF3949AB)
                Month.MARCH, Month.APRIL, Month.MAY -> Color(0xFF388E3C)
                Month.JUNE, Month.JULY, Month.AUGUST -> Color(0xFFFFA500)
                Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Color(0xFFF44336)
                else -> Color.Black
            }
            yearMonth to secondColor
        }.toMap()
    }

    private fun generateGradientColors(objects: List<DayWithTodosAndEvents>): Map<YearMonth, Brush> {
        val distinctMonths = objects.map { YearMonth.from(it.dayEntity.date) }.distinct()
        return distinctMonths.map { yearMonth ->
            val seasonColor = when (yearMonth.month) {
                Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A237E),
                        Color(0xFF3949AB),
                        Color(0xFF7986CB),

                        )
                )
                Month.MARCH, Month.APRIL, Month.MAY -> Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF43A047),
                        Color(0xFF81C784),

                        )
                )
                Month.JUNE, Month.JULY, Month.AUGUST -> Brush.verticalGradient(
                    listOf(
                        Color(0xFFE65100),
                        Color(0xFFFB8C00),
                        Color(0xFFFFB74D),
                    )
                )
                Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Brush.verticalGradient(
                    listOf(
                        Color(0xFFB71C1C),
                        Color(0xFFF44336),
                        Color(0xFFE57373),

                        )
                )
                else -> Brush.verticalGradient(listOf(Color.Black))
            }
            yearMonth to seasonColor
        }.toMap()
    }

    @Composable
    fun MonthHeader(
        currentMonth:YearMonth,
        onMonthChanged: (YearMonth) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onMonthChanged(currentMonth.minusMonths(1))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous month"
                )
            }

            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Button(
                onClick = {
                    onMonthChanged(currentMonth.plusMonths(1))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next month"
                )
            }
        }
    }
}