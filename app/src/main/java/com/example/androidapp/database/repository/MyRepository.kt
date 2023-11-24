package com.example.androidapp.database.repository

import androidx.lifecycle.LiveData
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class MyRepository(private val myDao: MyDao) {

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> = myDao.getAllDayEntitiesSortedByDate()

    val allTodoEntities: LiveData<List<TodoEntity>> = myDao.getAllTodoEntities();

    val allEventEntities: LiveData<List<EventEntity>> = myDao.getAllEventEntities()

    val allNotes: LiveData<List<Note>> = myDao.getAllNotes();

    suspend fun saveDayEntity(newDayEntity: DayEntity) {
        myDao.saveDayEntity(newDayEntity)
    }

    suspend fun saveTodoEntity(newTodoEntity: TodoEntity) {
        myDao.saveTodoEntity(newTodoEntity)
    }

    suspend fun saveEventEntity(newEventEntity: EventEntity) {
        myDao.saveEventEntity(newEventEntity)
    }

    suspend fun addNewNote(note: Note){
        myDao.addNewNote(note)
    }
    suspend fun updateNote(note: Note){
        myDao.updateNote(note)
    }

    fun deleteNote(note:Note){
        myDao.deleteNote(note)
    }
    fun getNoteById(noteId: Long): Note? {
        return myDao.getNoteById(noteId)
    }

    fun getDayByDate(date: LocalDate): DayEntity? {
        return myDao.getDayByDate(date)
    }

    fun getEventsByDayId(dayId: Long): DayWithEvents? {
        return myDao.getEventsByDayId(dayId)
    }

    fun getTodosByDayId(dayId: Long): DayWithTodos? {
        return myDao.getTodosByDayId(dayId)
    }

}