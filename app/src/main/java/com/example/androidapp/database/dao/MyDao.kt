package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithEvents
import com.example.androidapp.database.model.DayWithTodos
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity
import java.time.LocalDate

@Dao
interface MyDao {
    @Upsert
    suspend fun saveDayEntity(dayEntity: DayEntity)

    @Transaction
    @Query("SELECT * FROM day_data ORDER BY date ASC")
    fun getAllDayEntitiesSortedByDate(): LiveData<List<DayEntity>>

    @Transaction
    @Query("SELECT * FROM day_data WHERE date = :date")
    fun getDayByDate(date: LocalDate): DayEntity?

    @Upsert
    suspend fun saveEventEntity(event: EventEntity)

    @Query("SELECT * FROM events")
    fun getAllEventEntities(): LiveData<List<EventEntity>>

    /*
    @Transaction
    @Query("SELECT * FROM events WHERE dayForeignId = :dayEntityId")
    fun getAllForDayEntityId(dayEntityId: Long): LiveData<List<EventEntity>>
    */

    @Upsert
    suspend fun saveTodoEntity(todo: TodoEntity)

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
    fun getEventsByDayId(dayId: Long): DayWithEvents?

    @Transaction
    @Query("SELECT * FROM day_data WHERE dayId = :dayId")
    fun getTodosByDayId(dayId: Long): DayWithTodos?
}