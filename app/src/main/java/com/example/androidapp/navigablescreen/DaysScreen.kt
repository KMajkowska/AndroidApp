package com.example.androidapp.navigablescreen

import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.androidapp.AddBackgroundToComposables
import com.example.androidapp.HorizontalDivider
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import java.time.LocalDate
import java.util.Calendar

class DaysScreen(
    private val mDayViewModel: DayViewModel,
    private var chosenDate: LocalDate = LocalDate.now()
) : NavigableScreen() {

    override val screenName: String
        get() = "Days"

    override fun screenIcon(): ImageVector {
        return Icons.Default.Create
    }

    @Composable
    override fun ViewWithBackground() {
        AddBackgroundToComposables({ View() }, { NoteView(chosenDate, mDayViewModel) })
    }

    @Composable
    fun NoteView(chosenDate: LocalDate, mDayViewModel: DayViewModel) {
        val hasDayEntityBeenChanged = remember { mutableStateOf(false) }

        val dayEntity: DayEntity =
            mDayViewModel.getDayByDate(chosenDate) ?: DayEntity(date = chosenDate)

        val note = remember { mutableStateOf(dayEntity.note) }
        val dayTitle = remember { mutableStateOf(dayEntity.dayTitle) }

        DisposableEffect(dayEntity) {
            onDispose {
                Log.e("TEST", hasDayEntityBeenChanged.toString())
                Log.e("TEST", dayEntity.toString())

                if (hasDayEntityBeenChanged.value) {
                    dayEntity.note = note.value
                    dayEntity.dayTitle = dayTitle.value
                    mDayViewModel.saveDayEntity(dayEntity)
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
        }
    }

    @Composable
    override fun View() {
        val days = mDayViewModel.allDayEntitiesSortedByDate.observeAsState(initial = listOf()).value
        val calendarView = CalendarView(LocalContext.current)
        for (day in days) {
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
                        chosenDate = LocalDate.of(year, month + 1, dayOfMonth)
                    }
                }
            )
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
