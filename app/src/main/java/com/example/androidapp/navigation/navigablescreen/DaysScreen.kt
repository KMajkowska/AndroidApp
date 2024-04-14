package com.example.androidapp.navigation.navigablescreen

import android.os.Build
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.InlineTextEditor
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.TextEditorWithPreview
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.EventCategories
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SettingsViewModelFactory
import java.time.LocalDate
import java.util.Calendar

class DaysScreen(
    private val mDayViewModel: DayViewModel,
    private val localDate: LocalDate,
    private val onNoteClick: (Long, LocalDate) -> Unit
) : NavigableScreen() {

    @Composable
    override fun View() {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun ViewWithBackground() {
        var dayEntity by remember { mutableStateOf(mDayViewModel.getDayByDate(localDate)) }
        var selectedNote by remember { mutableStateOf(mDayViewModel.getNoteByDate(localDate)) }

        Surface(modifier = Modifier.testTag(TestTags.DAYS_SCREEN_VIEW)) {
            AddBackgroundToComposables({
                View { chosenDate ->
                    dayEntity = mDayViewModel.getDayByDate(chosenDate)
                    selectedNote = mDayViewModel.getNoteByDate(chosenDate)
                }
            }, {
                DayDataView(dayEntity, selectedNote)
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    fun DayDataView(dayEntity: DayEntity, selectedNote: Note?) {
        val hasDayEntityBeenChanged = remember { mutableStateOf(false) }
        var hasNoteBeenChanged by remember { mutableStateOf(false) }
        var currentDayTitle by remember { mutableStateOf(dayEntity.dayTitle) }
        var hasDayTitleChanged = remember { mutableStateOf(false) }

        DisposableEffect(
            dayEntity,
            selectedNote,
            hasDayEntityBeenChanged.value,
            hasNoteBeenChanged
        ) {
            onDispose {
                if ((currentDayTitle != dayEntity.dayTitle) || hasDayEntityBeenChanged.value ) {
                    currentDayTitle =  dayEntity.dayTitle
                    mDayViewModel.saveDayEntity(dayEntity)
                }

                if (hasNoteBeenChanged && selectedNote != null)
                    mDayViewModel.updateNote(selectedNote)

                mDayViewModel.deleteDayEntityIfEmpty(dayEntity.dayId!!)
            }
        }

        //add placeholder here to name a day
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            item {
                TextEditorWithPreview(
                    data = dayEntity.dayTitle,
                    placeholder = stringResource(id = R.string.day_title)
                ) { data, onCloseEditor ->
                    InlineTextEditor(
                        data = data,
                        hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                    ) { possibleNewData ->
                        dayEntity.dayTitle = possibleNewData
                        onCloseEditor(possibleNewData)
                        hasDayTitleChanged.value = true
                    }
                }
            }

            item {
                HorizontalDivider()

                NoteItem(selectedNote) { note ->
                    onNoteClick(note?.id ?: -1, dayEntity.date)
                    hasNoteBeenChanged = true
                }

                HorizontalDivider()
            }

            item {
                ToDoView(
                    dayEntity.dayId!!,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                )
                HorizontalDivider()
            }
            item {
                EventView(
                    dayEntity.dayId!!,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                )
            }
        }
        LaunchedEffect(hasDayTitleChanged.value) {
            if (hasDayTitleChanged.value) {
                hasDayTitleChanged.value = false
            }
        }
    }

    @Composable
    fun View(onChangeDate: (LocalDate) -> Unit) {
        val mSettingsViewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
        )
        val isDarkMode by mSettingsViewModel.isDarkTheme.observeAsState(false)
        val isUniqrnMode by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(false)


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AndroidView(
                    factory = { context ->
                        val themedContext = ContextThemeWrapper(
                            context,
                            if (isUniqrnMode && isDarkMode) R.style.CalendarTextAppearance_DarkUnicorn
                            else if (isDarkMode) R.style.CalendarTextAppearance_Dark
                            else if(isUniqrnMode) R.style.CalendarTextAppearance_Unicorn
                            else {
                                R.style.CalendarTextAppearance_Light
                            }
                        )
                        CalendarView(themedContext).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { calendarView ->
                        val calendar = Calendar.getInstance()
                        val chosenDateInMillis: Long = calendar.apply {
                            set(Calendar.YEAR, localDate.year)
                            set(Calendar.MONTH, localDate.monthValue - 1)
                            set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
                        }.timeInMillis

                        calendarView.setDate(chosenDateInMillis, false, true)
                        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                            onChangeDate(LocalDate.of(year, month + 1, dayOfMonth))
                        }
                    }
                )
            }
        }
    }


    @Composable
    fun ToDoView(
        dayId: Long,
        hasDayEntityBeenChanged: MutableState<Boolean>,
    ) {
        var isEditing by remember { mutableStateOf(false) }
        val todoList = mDayViewModel.getTodosByDayId(dayId).observeAsState(initial = listOf()).value

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    stringResource(id = R.string.todos),
                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                if (isEditing) {
                    InlineTextEditor(
                        data = "",
                        hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                    ) { possiblyChangedData ->
                        if (possiblyChangedData.isNotEmpty()) {
                            mDayViewModel.saveTodoEntity(
                                TodoEntity(
                                    dayForeignId = dayId,
                                    title = possiblyChangedData
                                )
                            )
                        }
                        isEditing = false
                    }
                } else {
                    IconButton(onClick = {
                        isEditing = true
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add new todo")
                    }
                }

                todoList.forEach { todo ->
                    var isChecked by remember { mutableStateOf(todo.isDone) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(18.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    isChecked = checked
                                    todo.isDone = isChecked
                                    mDayViewModel.saveTodoEntity(todo)
                                    hasDayEntityBeenChanged.value = true
                                }
                            )

                            Text(
                                text = todo.title,
                                modifier = Modifier.weight(1f),
                                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                        IconButton(
                            onClick = { mDayViewModel.deleteTodoEntity(todo) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                            hasDayEntityBeenChanged.value = true
                        }
                    }
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    fun EventView(
        dayId: Long,
        hasDayEntityBeenChanged: MutableState<Boolean>
    ) {
        val eventList =
            mDayViewModel.getEventsByDayId(dayId).observeAsState(initial = listOf()).value
        val setUpCategory = stringResource(id = R.string.event_category_general)
        val newEventCategory = remember { mutableStateOf(setUpCategory) }
        var isEditing by remember { mutableStateOf(false) }
        var isDropdownExpanded by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.onTertiaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    stringResource(id = R.string.event_list),
                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                )

                val categoryList = EventCategories.entries.toTypedArray()
                if (isEditing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable {
                                    isDropdownExpanded = !isDropdownExpanded
                                }
                        ) {
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = {
                                    isDropdownExpanded = false
                                },
                                Modifier.background(MaterialTheme.colorScheme.primaryContainer)

                            ) {

                                categoryList.forEach { category ->
                                    if (category.name == newEventCategory.value) {
                                        return@forEach
                                    }
                                    DropdownMenuItem(
                                        {
                                            Text(
                                                text = stringResource(id = category.resourceId),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            )
                                        },
                                        onClick = {
                                            newEventCategory.value =
                                                context.getString(category.resourceId)
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                            Text(
                                newEventCategory.value,
                                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(8.dp)
                            )
                        }

                        InlineTextEditor(
                            data = "",
                            hasDayEntityBeenChanged = hasDayEntityBeenChanged
                        ) { possibleNewEventTitle ->
                            if (possibleNewEventTitle.isNotEmpty()) {
                                mDayViewModel.saveEventEntity(
                                    EventEntity(
                                        dayForeignId = dayId,
                                        title = possibleNewEventTitle,
                                        category = newEventCategory.value
                                    )
                                )
                            }
                            isEditing = false
                            newEventCategory.value = categoryList[0].toString()
                        }


                    }
                } else {
                    IconButton(onClick = {
                        isEditing = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new event"
                        )
                    }
                }

                eventList.forEach { event ->
                    val eventCategory by remember { mutableStateOf(event.category) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(18.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            val icon = getCategoryIcon(eventCategory)
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Text(
                                text = event.title,
                                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }

                        IconButton(
                            onClick = { mDayViewModel.deleteEventEntity(event) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                            hasDayEntityBeenChanged.value = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {

        stringResource(EventCategories.GENERAL.resourceId) -> Icons.Default.CalendarMonth
        stringResource(EventCategories.PARTY.resourceId) -> Icons.Default.Celebration
        stringResource(EventCategories.SPORT.resourceId) -> Icons.Default.SportsBasketball
        stringResource(EventCategories.MEETING.resourceId) -> Icons.Default.Groups
        stringResource(EventCategories.FOOD.resourceId) -> Icons.Default.Fastfood
        stringResource(EventCategories.ENTERTAINMENT.resourceId) -> Icons.Default.Games
        else -> Icons.Default.CalendarMonth
    }

}

@Composable
fun NoteItem(note: Note?, onNoteClicked: (Note?) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNoteClicked(note) }
    ) {
        Column {
            if (note != null) {
                Text(
                    text = note.noteTitle,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                val lines = note.content.lines().take(2)
                lines.forEachIndexed { index, line ->
                    Text(
                        text = if (index == 1 && lines.size > 1) "$line..." else line,
                        style = TextStyle(fontSize = 16.sp),
                        maxLines = 1,  // Limit to one line
                        overflow = TextOverflow.Ellipsis  // Indicate that the text might be truncated
                    )
                }
            }
            else {
                Text(
                    text = stringResource(id = R.string.no_note),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )

            }
        }

    }
}
