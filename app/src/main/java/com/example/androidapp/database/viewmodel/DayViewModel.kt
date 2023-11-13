package com.example.androidapp.database.viewmodel

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.database.DatabaseConnection
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.repository.MyRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DayViewModel(application: Application) : AndroidViewModel(application) {

    private val myDao: MyDao = DatabaseConnection.getDatabase(application).myDao()
    private val repository: MyRepository = MyRepository(myDao)

    val allDayEntities: LiveData<List<DayEntity>> = repository.allDayEntities
    val allTodoEntities: LiveData<List<TodoEntity>> = repository.allTodoEntities
    val allEventEntities: LiveData<List<EventEntity>> = repository.allEventEntities

    fun insertDayEntity(dayEntity: DayEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDayEntity(dayEntity)
        }
    }

    fun insertTodoEntity(todoEntity: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTodoEntity(todoEntity)
        }
    }

    fun insertEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEventEntity(eventEntity)
        }
    }

    suspend fun getDayByDate(date: String): DayEntity {
        val deferred: Deferred<DayEntity> = viewModelScope.async(Dispatchers.IO) {
            repository.getDayByDate(date)!!
        }
        return deferred.await()
    }

    suspend fun getEventsByDayId(dayId: Long): DayWithEvents {
        val deferred: Deferred<DayWithEvents> = viewModelScope.async(Dispatchers.IO) {
            myDao.getEventsByDayId(dayId)
        }
        return deferred.await()
    }

    suspend fun getTodosByDayId(dayId: Long): DayWithTodos {
        val deferred: Deferred<DayWithTodos> = viewModelScope.async(Dispatchers.IO) {
            myDao.getTodosByDayId(dayId)
        }
        return deferred.await()
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