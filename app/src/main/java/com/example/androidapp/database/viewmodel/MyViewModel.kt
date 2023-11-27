package com.example.androidapp.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.androidapp.database.MyDatabaseConnection
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.repository.MyRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayViewModel(application: Application) : AndroidViewModel(application) {

    private val myDao: MyDao = MyDatabaseConnection.getDatabase(application).myDao()
    private val repository: MyRepository = MyRepository(myDao)

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> = repository.allDayEntitiesSortedByDate
    val allTodoEntities: LiveData<List<TodoEntity>> = repository.allTodoEntities
    val allEventEntities: LiveData<List<EventEntity>> = repository.allEventEntities
    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun saveDayEntity(dayEntity: DayEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDayEntity(dayEntity)
        }
    }

    fun saveDayEntityWithTodos(dayWithTodos: DayWithTodos?, todos: List<TodoEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDayEntityWithTodos(dayWithTodos, todos)
        }
    }

    fun saveTodoEntity(todoEntity: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTodoEntity(todoEntity)
        }
    }
    fun getDayWithTodosByDate(date: LocalDate): DayWithTodos? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayWithTodosByDate(date)
            }
        }
    }

    fun saveEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveEventEntity(eventEntity)
        }
    }

    fun addNewNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewNote(note)
        }
    }


    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun getNoteById(id: Long): Note?{
        return runBlocking {
            withContext(Dispatchers.IO){
                repository.getNoteById(id)
            }
        }
    }
    fun getDayByDate(date: LocalDate): DayEntity? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayByDate(date)
            }
        }
    }
    fun getEventsByDayId(dayId: Long): DayWithEvents {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getEventsByDayId(dayId)
            }
        }
    }

    fun getTodosByDayId(dayId: Long): DayWithTodos {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getTodosByDayId(dayId)
            }
        }
    }

    fun insertTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.addNewTodo(todo)
        }
    }

    fun getTodosByDay(date: LocalDate): DayWithTodos {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayWithTodosByDate(date)
            }
        }
    }

}

class DayViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DayViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}