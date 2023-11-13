package com.example.androidapp.database.repository

import androidx.lifecycle.LiveData
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity

class MyRepository(private val myDao: MyDao) {

    val allDayEntities: LiveData<List<DayEntity>> = myDao.getAllDayEntities()

    val allTodoEntities: LiveData<List<TodoEntity>> = myDao.getAllTodoEntities();

    val allEventEntities: LiveData<List<EventEntity>> = myDao.getAllEventEntities()

    suspend fun insertDayEntity(newDayEntity: DayEntity) {
        myDao.insertDayEntity(newDayEntity)
    }

    suspend fun insertTodoEntity(newTodoEntity: TodoEntity) {
        myDao.insertTodoEntity(newTodoEntity)
    }

    suspend fun insertEventEntity(newEventEntity: EventEntity) {
        myDao.insertEventEntity(newEventEntity)
    }

    fun getDayByDate(date: String): DayEntity? {
        return myDao.getDayByDate(date)
    }

    fun getEventsByDayId(dayId: Long): DayWithEvents? {
        return myDao.getEventsByDayId(dayId)
    }

    fun getTodosByDayId(dayId: Long): DayWithTodos? {
        return myDao.getTodosByDayId(dayId)
    }

}