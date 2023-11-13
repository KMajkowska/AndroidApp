package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity

@Dao
interface MyDao {
    @Insert
    suspend fun insertDayEntity(dayEntity: DayEntity)

    @Transaction
    @Query("SELECT * FROM day_data")
    fun getAllDayEntities(): LiveData<List<DayEntity>>

    @Transaction
    @Query("SELECT * FROM day_data WHERE date = :date")
    fun getDayByDate(date: String): DayEntity

    @Insert
    suspend fun insertEventEntity(event: EventEntity)

    @Query("SELECT * FROM events")
    fun getAllEventEntities(): LiveData<List<EventEntity>>

    /*
    @Transaction
    @Query("SELECT * FROM events WHERE dayForeignId = :dayEntityId")
    fun getAllForDayEntityId(dayEntityId: Long): LiveData<List<EventEntity>>
    */

    @Insert
    suspend fun insertTodoEntity(todo: TodoEntity)

    @Transaction
    @Query("SELECT * FROM todos")
    fun getAllTodoEntities(): LiveData<List<TodoEntity>>

    /*
    @Transaction
    @Query("SELECT * FROM todos WHERE dayForeignId = :dayEntityId")
    fun getAllTodoEntitiesForDayEntityId(dayEntityId: Long): LiveData<List<TodoEntity>>
     */

    @Transaction
    @Query("SELECT * FROM day_data WHERE dayId = :dayId")
    fun getEventsByDayId(dayId: Long): DayWithEvents

    @Transaction
    @Query("SELECT * FROM day_data WHERE dayId = :dayId")
    fun getTodosByDayId(dayId: Long): DayWithTodos
}