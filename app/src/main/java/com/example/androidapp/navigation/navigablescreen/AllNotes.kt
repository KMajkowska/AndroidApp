package com.example.androidapp.navigation.navigablescreen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.R
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.ui.theme.Blue
import java.time.LocalDate

class AllNotes(
    private val mDayViewModel: DayViewModel,
    private val localDate: LocalDate,
    private val onNoteClick: (Long) -> Unit,
    private val onCalendarClick: (LocalDate) -> Unit
   ) : NavigableScreen() {

    @Composable
    override fun View() {
        val notes = mDayViewModel.allNotes.observeAsState(initial = listOf()).value

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                            onNoteClicked = { selectedNote -> onNoteClick(selectedNote.noteId) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = {
                        onNoteClick(-1) // negative value means that no note will be found!
                    },
                    modifier = Modifier
                        .size(66.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Blue, shape = CircleShape)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
    }
    @Composable
    fun NoteItem(note: Note, onNoteClicked: (Note) -> Unit) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(8.dp)
                .clickable { onNoteClicked(note) }
        ) {
            Column {
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
                if (note.noteDate != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${note.noteDate}",
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.weight(1f)
                        )

                        // Add calendar icon for notes with a date
                        if (note.noteDate != null) {
                            IconButton(
                                //redirect to calendar screen with a specific date
                                onClick = {
                                    note.noteDate?.let{
                                        date -> onCalendarClick(date)
                                    }
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}