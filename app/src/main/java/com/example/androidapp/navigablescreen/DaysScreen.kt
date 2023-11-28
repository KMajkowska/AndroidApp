package com.example.androidapp.navigablescreen

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate
import java.util.Calendar

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
        val chosenDate =  remember { mutableStateOf(LocalDate.now()) }

        AddBackgroundToComposables({ View(chosenDate) }, { DayDataView(chosenDate, mDayViewModel) })
    }

    @Composable
    fun DayDataView(chosenDate: MutableState<LocalDate>, mDayViewModel: DayViewModel) {
        val hasDayEntityBeenChanged = remember { mutableStateOf(false) }
        val dayEntity: DayEntity = mDayViewModel.getDayByDate(chosenDate.value)
            ?: mDayViewModel.saveAndRetrieveDayEntity(chosenDate.value)

        val note = remember { mutableStateOf(dayEntity.note) }
        val dayTitle = remember { mutableStateOf(dayEntity.dayTitle) }

        DisposableEffect(dayEntity) {
            onDispose {
                if (hasDayEntityBeenChanged.value) {
                    dayEntity.note = note.value
                    dayEntity.dayTitle = dayTitle.value
                    mDayViewModel.saveDayEntity(dayEntity)
                }

                dayTitle.value = ""
                note.value = ""
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            item {
                TextEditorWithPreview(
                    data = dayTitle,
                    textEditor = { data, onCloseEditor ->
                        InlineTextEditor(
                            data = data,
                            hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                            onCloseEditor = onCloseEditor
                        )
                    }
                )
                HorizontalDivider()
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Yellow)
                ) {
                    TextEditorWithPreview(
                        data = note,
                        textEditor = { data, onCloseEditor ->
                            DialogTextEditor(
                                data = data,
                                hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                                onCloseEditor = onCloseEditor
                            )
                        }
                    )
                }
            }
            item {
                ToDoView(
                    dayEntity.dayId!!,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                )
            }
            item {
                EventView(
                    onEventIconSelected = { index, category ->
                        // Handle selecting event icon as needed
                        // For simplicity, let's use a default icon here
                        val icon = "📅"
                        val event = Pair(icon, category)
                    },
                    onAddEvent = { category, eventName ->
                    }
                )
            }
        }
    }

    @Composable
    fun View(chosenDate: MutableState<LocalDate>) {
        val calendarView = CalendarView(LocalContext.current)
        for (day in mDayViewModel.allDayEntitiesSortedByDate.observeAsState(initial = listOf()).value) {
            val calendar = Calendar.getInstance()
            calendar.set(day.date.year, day.date.monthValue - 1, day.date.dayOfMonth)
            calendarView.setDate(calendar.timeInMillis, true, true)
        }

        Row(
            modifier = Modifier
                .padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {

            AndroidView(
                { calendarView },
                modifier = Modifier.wrapContentWidth(),
                update = {
                    it.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        chosenDate.value = LocalDate.of(year, month + 1, dayOfMonth)
                    }
                }
            )
        }
    }


    @Composable
    fun ToDoView(
        dayId: Long,
        hasDayEntityBeenChanged: MutableState<Boolean>
    ) {
        var isEditing by remember { mutableStateOf(false) }
        val todoList = mDayViewModel.getTodosByDayId(dayId).observeAsState(initial = listOf()).value
        val newTodoItem = remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text("To do")

            if (isEditing) {
                InlineTextEditor(
                    data = newTodoItem,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged
                ) {
                    if (newTodoItem.value.isNotEmpty()) {
                        mDayViewModel.saveTodoEntity(
                            TodoEntity(
                                dayForeignId = dayId,
                                title = newTodoItem.value
                            )
                        )
                    }
                    isEditing = false
                    newTodoItem.value = ""
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
            }
        }
    }
}

@Composable
fun NotePreview(noteData: MutableState<String>, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable { onEditClick() }
    ) {
        Text(
            text = noteData.value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun TextEditorWithPreview(
    data: MutableState<String>,
    textEditor: @Composable (data: MutableState<String>, onCloseEditor: () -> Unit) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        textEditor(data) {
            isEditing = false
        }
    } else {
        NotePreview(noteData = data) {
            isEditing = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InlineTextEditor(
    data: MutableState<String>,
    hasDayEntityBeenChanged: MutableState<Boolean>,
    onCloseEditor: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var tempText by remember { mutableStateOf(data.value) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = tempText,
            onValueChange = {
                tempText = it
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    keyboardController?.hide()
                    onCloseEditor()
                }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
            }

            Button(
                onClick = {
                    data.value = tempText
                    hasDayEntityBeenChanged.value = true
                    keyboardController?.hide()
                    onCloseEditor()
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogTextEditor(
    data: MutableState<String>,
    hasDayEntityBeenChanged: MutableState<Boolean>,
    onCloseEditor: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var tempText by remember { mutableStateOf(data.value) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {

        Dialog(
            onDismissRequest = {
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = tempText,
                    onValueChange = {
                        tempText = it
                    },
                    label = {
                        Text("Content")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            onCloseEditor()
                            showDialog = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                    }

                    Button(
                        onClick = {
                            data.value = tempText
                            hasDayEntityBeenChanged.value = true
                            keyboardController?.hide()
                            onCloseEditor()
                            showDialog = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EventView(
    onEventIconSelected: (Int, String) -> Unit,
    onAddEvent: (String, String) -> Unit
) {
    // Create an empty mutable state list to store event items
    val eventList = remember { mutableStateListOf<Pair<String, String>>() }

    // Create a local variable for the new event item
    var newEventItem by remember { mutableStateOf("") }

    // Create a local variable for the selected category
    var selectedCategory by remember { mutableStateOf("General") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text("Event List")

        // Display existing event items with categories and names if the list is not empty
        if (eventList.isNotEmpty()) {
            eventList.forEachIndexed { index, (category, eventName) ->
                Row(
                    modifier = Modifier.clickable { /* Handle item click if needed */ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Map the category to the corresponding icon
                        val icon = getCategoryIcon(category)

                        Icon(imageVector = icon, contentDescription = null)
                        Text(eventName)
                    }
                }
            }
        } else {
            // Show a message when the event list is empty
            Text("No events.")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field for adding new event items
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var expanded by remember { mutableStateOf(false) }

            // Dropdown toggle for new event items
            Box(
                modifier = Modifier.clickable(onClick = { expanded = !expanded })
            ) {
                Text(selectedCategory)

                // Dropdown menu for new event items
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Define a list of categories for the dropdown
                    val categoryList = listOf("General", "Party", "Sports", "Meeting", "Food")

                    // Display categories in the dropdown
                    categoryList.forEach { category ->
                        DropdownMenuItem(
                            { Text(category, Modifier.padding(24.dp)) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            })
                    }
                }
            }

            TextField(
                value = newEventItem,
                onValueChange = { newEventItem = it },
                label = { Text("Add an event") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            if (newEventItem.isNotBlank()) {
                                eventList.add(Pair(selectedCategory, newEventItem))
                                onAddEvent(selectedCategory, newEventItem)
                                newEventItem = ""
                            }
                            true
                        } else {
                            false
                        }
                    }
            )
        }
    }
}

@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "General" -> Icons.Default.CalendarMonth
        "Party" -> Icons.Default.Cake
        "Sports" -> Icons.Default.SportsBasketball
        "Meeting" -> Icons.Default.MeetingRoom
        "Food" -> Icons.Default.Fastfood
        "Entertainment" -> Icons.Default.Celebration
        else -> Icons.Default.CalendarMonth
    }
}
