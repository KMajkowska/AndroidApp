package com.example.androidapp.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidapp.database.MyDatabaseConnection
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.repository.MyRepository
import com.example.androidapp.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayViewModel(
    application: Application,
    private val notificationHelper: NotificationHelper,
    databaseConnection: MyDatabaseConnection
) : AndroidViewModel(application) {

    private val repository: MyRepository = MyRepository(
        databaseConnection.dayDao(),
        databaseConnection.eventDao(),
        databaseConnection.noteDao(),
        databaseConnection.todoDao(),
        databaseConnection.connectedToNoteDao()
    )

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> =
        repository.allDayEntitiesSortedByDate
    val allDayEntitiesWithRelatedSortedByDate: LiveData<List<DayWithTodosAndEvents>> =
        repository.allDayEntitiesWithRelatedSortedByDate
    val allTodoEntities: LiveData<List<TodoEntity>> = repository.allTodoEntities
    val allEventEntities: LiveData<List<EventEntity>> = repository.allEventEntities

    val allConnectedToNote: LiveData<List<ConnectedToNote>> = repository.allConnectedToNotes

    val allNotes: LiveData<List<Note>> = repository.allNotes


    fun getConnectedToNoteById(id: Long): ConnectedToNote? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getConnectedToNoteById(id)
            }
        }
    }

    fun addConnectedToNoteText(connectedToNote: ConnectedToNote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewConnectedToNote(connectedToNote)
        }
    }

    fun deleteConnectedToDay(connectedToNote: ConnectedToNote) {
        viewModelScope.launch(Dispatchers.IO) {
            connectedToNote.doBeforeDeletingRecord()
            repository.deleteConnectedToNote(connectedToNote)
        }
    }

    fun updateConnectedToNote(connectedToNote: ConnectedToNote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateConnectedToNote(connectedToNote)
        }
    }
    private fun scheduleNotification(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationHelper.scheduleNotification(repository.getDayIdWithRelatedByDate(date))
        }
    }

    fun saveDayEntity(dayEntity: DayEntity) {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.saveDayEntity(dayEntity)
                scheduleNotification(dayEntity.date)
            }
        }
    }

    fun deleteDayEntity(dayEntity: DayEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDayEntity(dayEntity)
            notificationHelper.unscheduleNotification(dayEntity.dayId)
        }
    }

    private fun saveAndRetrieveDayEntity(date: LocalDate): DayEntity {
        val dayEntity = DayEntity(date = date)
        saveDayEntity(dayEntity)
        return getInnerDayByDate(date)!!
    }

    fun getDayByDate(chosenDate: LocalDate): DayEntity {
        return getInnerDayByDate(chosenDate) ?: saveAndRetrieveDayEntity(chosenDate)
    }

    fun getNoteByDate(chosenDate: LocalDate): Note? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getNoteByDate(chosenDate)
            }
        }
    }

    fun deleteNoteEntity(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNoteEntity(note)
        }
    }

    fun saveTodoEntity(todoEntity: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTodoEntity(todoEntity)
        }
    }

    fun deleteTodoEntity(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTodoEntity(todo)
        }
    }

    fun getInnerDayByDate(date: LocalDate): DayEntity? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayByDate(date)
            }
        }
    }

    fun saveEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveEventEntity(eventEntity)
        }
    }

    fun deleteEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventEntity(eventEntity)
        }
    }

    fun addNewNote(note: Note) {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.addNewNote(note)
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun getNoteById(id: Long): Note? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getNoteById(id)
            }
        }
    }

    fun getConnectedToNoteByNoteId(noteId: Long): LiveData<List<ConnectedToNote>> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getConnectedToNoteByNoteId(noteId)
            }
        }
    }

    fun getEventsByDayId(dayId: Long): LiveData<List<EventEntity>> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getEventsByDayId(dayId)
            }
        }
    }

    fun getTodosByDayId(dayId: Long): LiveData<List<TodoEntity>> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getTodosByDayId(dayId)
            }
        }
    }

    private fun getDayIdWithRelated(dayId: Long): DayWithTodosAndEvents? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayIdWithRelated(dayId)
            }
        }
    }

    fun deleteDayEntityIfEmpty(dayId: Long) {
        val temp: DayWithTodosAndEvents? = getDayIdWithRelated(dayId)
        if (temp?.events?.isEmpty() == true &&
            temp.todos.isEmpty() &&
            temp.dayEntity.dayTitle.trim().isEmpty()
        )
            deleteDayEntity(temp.dayEntity)
    }
}

class DayViewModelFactory(
    private val application: Application,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DayViewModel(
                application,
                notificationHelper,
                MyDatabaseConnection.getDatabase(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
