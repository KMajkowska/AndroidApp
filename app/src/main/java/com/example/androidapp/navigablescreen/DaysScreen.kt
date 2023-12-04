package com.example.androidapp.navigablescreen


import android.content.Intent
import android.widget.CalendarView
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.DialogTextEditor
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.InlineTextEditor
import com.example.androidapp.TextEditorWithPreview
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.EventCategories
import java.time.LocalDate

class DaysScreen(private val mDayViewModel: DayViewModel) : NavigableScreen() {

    override val screenName: String
        get() = "Days"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Create
    }

    @Composable
    override fun View() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun ViewWithBackground() {
        var dayEntity by remember { mutableStateOf(mDayViewModel.getDayByDate(LocalDate.now())) }

        AddBackgroundToComposables({
            View { chosenDate ->
                dayEntity = mDayViewModel.getDayByDate(chosenDate)
            }
        }, {
            DayDataView(dayEntity)
        })
    }

    @Composable
    fun DayDataView(dayEntity: DayEntity) {
        val hasDayEntityBeenChanged = remember { mutableStateOf(false) }
        val context = LocalContext.current

        DisposableEffect(dayEntity, hasDayEntityBeenChanged.value) {
            onDispose {
                if (hasDayEntityBeenChanged.value)
                    mDayViewModel.saveDayEntity(dayEntity)
                mDayViewModel.deleteDayEntityIfEmpty(dayEntity.dayId!!)
            }
        }


        val date = dayEntity.date
        val dayNote = mDayViewModel.getNoteByDate(date)
        var selectedNote by remember { mutableStateOf(mDayViewModel.getNoteByDate(date))}
        var isUpdated by remember { mutableStateOf(false) }


        DisposableEffect(selectedNote, isUpdated) {
            onDispose {
                if (isUpdated) {
                    selectedNote?.let {
                        mDayViewModel.updateNote(it)
                    }
                    isUpdated = false
                }
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

                NoteItem(note = dayNote) { note ->
                    selectedNote = note

                    if (selectedNote == null) {
                        // Check if a note already exists for the date
                        val existingNote = mDayViewModel.getNoteByDate(date)

                        if (existingNote == null) {
                            // If no note exists, create a new one
                            val newNote = Note(
                                noteTitle = "My note for ${date}",
                                content = "",
                                noteDate = date
                            )
                            mDayViewModel.addNewNote(newNote)

                            // Set selectedNote to the newly created note
                            selectedNote = newNote
                            isUpdated = true
                        }
                    } else {
                        val intent = Intent(context, CreateNote::class.java)
                        intent.putExtra("noteId", selectedNote!!.noteId)
                        context.startActivity(intent)
                        selectedNote = mDayViewModel.getNoteByDate(date)
                        isUpdated = true
                    }
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
    }

    @Composable
    fun View(onChangeDate: (possiblyChangedDate: LocalDate) -> Unit) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {

            AndroidView(
                { CalendarView(it) },
                modifier = Modifier.wrapContentWidth(),
                update = {
                    it.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        onChangeDate(LocalDate.of(year, month + 1, dayOfMonth))
                    }
                }
            )
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
        val newEventCategory = remember { mutableStateOf("General") }
        var isEditing by remember { mutableStateOf(false) }
        var isDropdownExpanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text("Event List")
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
                                    Text(category.name, Modifier.padding(8.dp))
                                },
                                onClick = {
                                    newEventCategory.value = category.name
                                    hasDayEntityBeenChanged.value = true
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
                    text = "${note.noteTitle}",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                val lines = note.content.lines().take(2)
                lines.forEachIndexed { index, line ->
                    Text(
                        text = if (index == 1 && lines.size > 1) "$line..." else line,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
            else {
                Text(
                    text = "No note available - click to add!",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )

            }
        }

    }
}



