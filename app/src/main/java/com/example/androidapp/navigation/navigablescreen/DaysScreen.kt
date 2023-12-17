package com.example.androidapp.navigation.navigablescreen

import android.view.ViewGroup
import android.widget.CalendarView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.InlineTextEditor
import com.example.androidapp.R
import com.example.androidapp.TextEditorWithPreview
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.EventCategories
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

    @Composable
    override fun ViewWithBackground() {
        var dayEntity by remember { mutableStateOf(mDayViewModel.getDayByDate(localDate)) }
        var selectedNote by remember { mutableStateOf(mDayViewModel.getNoteByDate(dayEntity.date))}

        AddBackgroundToComposables({
            View { chosenDate ->
                dayEntity = mDayViewModel.getDayByDate(chosenDate)
                selectedNote = mDayViewModel.getNoteByDate(dayEntity.date)
            }
        }, {
            DayDataView(dayEntity, selectedNote)
        })
    }

    @Composable
    fun DayDataView(dayEntity: DayEntity, selectedNote: Note?) {
        val hasDayEntityBeenChanged = remember { mutableStateOf(false) }
        var hasNoteBeenChanged by remember { mutableStateOf(false) }
        var currentDayTitle by remember { mutableStateOf(dayEntity.dayTitle) }
        val hasDayTitleChanged = remember { mutableStateOf(false) }

        DisposableEffect(dayEntity, selectedNote, hasDayEntityBeenChanged.value, hasNoteBeenChanged) {
            onDispose {
                if (hasDayEntityBeenChanged.value) {
                    mDayViewModel.saveDayEntity(dayEntity)
                    currentDayTitle = dayEntity.dayTitle
                }


                if (hasNoteBeenChanged && selectedNote != null)
                    mDayViewModel.updateNote(selectedNote)

                mDayViewModel.deleteDayEntityIfEmpty(dayEntity.dayId!!)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            item {
                TextEditorWithPreview(
                    data = dayEntity.dayTitle
                ) { data, onCloseEditor ->
                    InlineTextEditor(
                        data = data,
                        hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                    ) { possibleNewData ->
                        dayEntity.dayTitle = possibleNewData
                        onCloseEditor(possibleNewData)
                    }
                }
            }



            item {
                HorizontalDivider()

                NoteItem(selectedNote) { note ->
                    onNoteClick(note?.noteId ?: -1, dayEntity.date)
                    hasNoteBeenChanged = true
                }

                HorizontalDivider()
            }

            item {
                ToDoView(
                    dayEntity.dayId!!,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                )
            }
            item {
                EventView(
                    dayEntity.dayId!!,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                )
            }
        }
        DisposableEffect(hasDayTitleChanged.value) {
            if (hasDayTitleChanged.value) {
                // Day title has changed, update your UI or perform any other actions
                // This block will be executed when the day title changes
                hasDayTitleChanged.value = false // Reset the flag
            }
            onDispose { }
        }
    }

    @Composable
    fun View(onChangeDate: (LocalDate) -> Unit) {
        LazyColumn(modifier = Modifier.fillMaxSize()){
            item{
                AndroidView(
                    factory = { context ->
                        CalendarView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {
                        if (localDate != null) {
                            val calendar = Calendar.getInstance()
                            val chosenDateInMillis: Long = calendar.apply {
                                set(Calendar.YEAR, localDate.year)
                                set(Calendar.MONTH, localDate.monthValue - 1)
                                set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
                            }.timeInMillis

                            it.setDate(chosenDateInMillis, false, true)
                        }
                        it.setOnDateChangeListener { _, year, month, dayOfMonth ->
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text("To do")

            if (isEditing) {
                InlineTextEditor(
                    data = "",
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged
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
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                        Text(text = todo.title)
                    }

                    // Added delete button
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(stringResource(id = R.string.event_list))
        }

        HorizontalDivider()

        val categoryList = EventCategories.values()
        if (isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                        }
                    ) {

                        categoryList.forEach { category ->
                            if (category.name == newEventCategory.value) {
                                return@forEach
                            }
                            DropdownMenuItem(
                                {
                                    Text(
                                        text = stringResource(id = category.resourceId),
                                        Modifier.padding(8.dp)
                                    )
                                },
                                onClick = {
                                    newEventCategory.value = context.getString(category.resourceId)
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                    Text(newEventCategory.value)
                }

                Spacer(modifier = Modifier.width(8.dp))

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
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val icon = getCategoryIcon(eventCategory)
                    Icon(imageVector = icon, contentDescription = null)
                    Text(event.title)
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

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {

        EventCategories.GENERAL.name -> Icons.Default.CalendarMonth
        EventCategories.PARTY.name -> Icons.Default.Celebration
        EventCategories.SPORT.name -> Icons.Default.SportsBasketball
        EventCategories.MEETING.name -> Icons.Default.Groups
        EventCategories.FOOD.name -> Icons.Default.Fastfood
        EventCategories.ENTERTAINMENT.name -> Icons.Default.Games
        else -> Icons.Default.CalendarMonth
    }
}

@Composable
fun NoteItem(note: Note?, onNoteClicked: (Note?) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
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
