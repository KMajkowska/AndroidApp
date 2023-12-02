package com.example.androidapp.navigablescreen

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.database.model.EventEntity
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
        val chosenDate =  remember { mutableStateOf(LocalDate.now()) }

        AddBackgroundToComposables({ View(chosenDate) }, { DayDataView(chosenDate, mDayViewModel) })
    }

        @Composable
        fun DayDataView(chosenDate: MutableState<LocalDate>, mDayViewModel: DayViewModel) {
            val hasDayEntityBeenChanged = remember { mutableStateOf(false) }
            val dayEntity = mDayViewModel.getDayByDate(chosenDate.value)
                ?: mDayViewModel.saveAndRetrieveDayEntity(chosenDate.value)
            Log.e("TEST", dayEntity.toString())
            DisposableEffect(dayEntity) {
                onDispose {
                    if (hasDayEntityBeenChanged.value)
                       mDayViewModel.saveDayEntity(dayEntity)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                item {
                    TextEditorWithPreview(
                        data = dayEntity.dayTitle,
                        textEditor = { data, onCloseEditor ->
                            InlineTextEditor(
                                data = data,
                                hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                            ) {
                                onCloseEditor()
                                dayEntity.dayTitle = data.value
                            }
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
                            data = dayEntity.note,
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
                        dayEntity.dayId!!,
                        hasDayEntityBeenChanged = hasDayEntityBeenChanged,
                    )
                }
            }
        }

    @Composable
    fun View(chosenDate: MutableState<LocalDate>) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {

            AndroidView(
                { CalendarView(it) },
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
                    horizontalArrangement = Arrangement.SpaceBetween, // Added arrangement for space between elements
                    modifier = Modifier.padding(16.dp) // Added padding for better spacing
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
                        onClick = {mDayViewModel.deleteTodoEntity(todo)}
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
        val newEventTitle = remember { mutableStateOf("") }
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
                    data = newEventTitle,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged
                ) {
                    if (newEventTitle.value.isNotEmpty()) {
                        mDayViewModel.saveEventEntity(
                            EventEntity(
                                dayForeignId = dayId,
                                title = newEventTitle.value,
                                category = newEventCategory.value
                            )
                        )
                    }
                    isEditing = false
                    newEventCategory.value = categoryList[0].toString()
                    newEventTitle.value = ""
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
    data: String,
    textEditor: @Composable (data: MutableState<String>, onCloseEditor: () -> Unit) -> Unit
) {
    val dataComposable = remember { mutableStateOf(data) }
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        textEditor(dataComposable) {
            isEditing = false
        }
    } else {
        NotePreview(noteData = dataComposable) {
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
    Log.e("TEST", tempText)

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
fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        EventCategories.GENERAL.name-> Icons.Default.CalendarMonth
        EventCategories.PARTY.name -> Icons.Default.Cake
        EventCategories.SPORT.name -> Icons.Default.SportsBasketball
        EventCategories.MEETING.name -> Icons.Default.MeetingRoom
        EventCategories.FOOD.name -> Icons.Default.Fastfood
        else -> Icons.Default.CalendarMonth
    }
}
