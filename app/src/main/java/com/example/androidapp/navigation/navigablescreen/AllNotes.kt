package com.example.androidapp.navigation.navigablescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.R
import com.example.androidapp.TestTags
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.settings.NoteSortOptionEnum
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SettingsViewModelFactory
import com.example.androidapp.ui.theme.Blue
import java.time.LocalDate


class AllNotes(
    private val mDayViewModel: DayViewModel,
    private val localDate: LocalDate,
    private val onNoteClick: (Long) -> Unit,
    private val onCalendarClick: (LocalDate) -> Unit,
   ) : NavigableScreen() {


@Composable
    override fun View() {
        var notes = mDayViewModel.allNotes.observeAsState(initial = listOf()).value

        val mSettingsViewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
        )
        val sortOption by mSettingsViewModel.selectedSortOption.observeAsState(NoteSortOptionEnum.ASCENDING)

        if (sortOption == NoteSortOptionEnum.DESCENDING)
            notes = notes.reversed()

        val isVisible = rememberSaveable { mutableStateOf(true) }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    // Hide FAB
                    if (available.y < -1) {
                        isVisible.value = false
                    }
                    // Show FAB
                    if (available.y > 1) {
                        isVisible.value = true
                    }
                    return Offset.Zero
                }
            }
        }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible.value,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            )  {
                FloatingActionButton(
                    onClick = { onNoteClick(-1) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Create note",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }

            }

        }){
            innerPadding ->
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .nestedScroll(nestedScrollConnection)
            .testTag(TestTags.ALL_NOTES_VIEW),
    ){
            item{
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (notes.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.all_notes),
                                style = TextStyle(fontSize = 24.sp)
                            )
                        } else {
                            for (note in notes) {
                                NoteItem(
                                    note = note,
                                    onNoteClicked = { selectedNote -> onNoteClick(selectedNote.noteId!!) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                }
            }
        }
    }
    }

    @Composable
    fun NoteItem(note: Note, onNoteClicked: (Note) -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp, 0.dp, 6.dp, 6.dp)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onNoteClicked(note) }
        ) {
            Column {
                Text(
                    text = note.noteTitle,
                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(10.dp, 2.dp, 10.dp, 2.dp)
                        .testTag(TestTags.DISPLAYED_NOTE_TITLE)
                )
                val lines = note.content.lines().take(2)
                lines.forEachIndexed { index, line ->
                    Text(
                        text = if (index == 1 && lines.size > 1) "$line..." else line,
                        style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp),
                        maxLines = 1,  // Limit to one line
                        overflow = TextOverflow.Ellipsis,  // Indicate that the text might be truncated
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 10.dp, 0.dp)
                            .testTag(TestTags.DISPLAYED_NOTE_CONTENT)
                    )
                }
                if (note.noteDate != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${note.noteDate}",
                            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontStyle = FontStyle.Italic),
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp, 0.dp, 10.dp, 0.dp)
                        )
                        if (note.noteDate != null) {
                            IconButton(
                                //redirect to days screen with a specific date
                                onClick = {
                                    note.noteDate?.let{
                                            date -> onCalendarClick(date)
                                    }
                                },
                                modifier = Modifier
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Redirect to days",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }


                    }
                }
            }

        }
        Divider(
            modifier = Modifier
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onBackground)
                .fillMaxWidth()
        )
    }
}

