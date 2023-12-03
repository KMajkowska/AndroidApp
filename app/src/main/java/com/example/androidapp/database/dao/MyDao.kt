package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import java.time.LocalDate

@Dao
interface MyDao {
    @Upsert
    suspend fun saveDayEntity(dayEntity: DayEntity)

    @Delete
    suspend fun deleteDayEntity(dayEntity: DayEntity)

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

    @Delete
    suspend fun deleteTodoEntity(todo: TodoEntity)

    @Delete
    suspend fun deleteEventEntity(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

    @Transaction
    @Query("SELECT * FROM todos")
    fun getAllTodoEntities(): LiveData<List<TodoEntity>>

    @Transaction
    @Query("SELECT * FROM day_data WHERE dayId = :dayId")
    fun getDayIdWithRelated(dayId: Long): DayWithTodosAndEvents?

    @Transaction
    @Query("SELECT * FROM day_data ORDER BY date ASC")
    fun getAllDayEntitiesWithRelatedSortedByDate(): LiveData<List<DayWithTodosAndEvents>>
    /*
    @Transaction
    @Query("SELECT * FROM todos WHERE dayForeignId = :dayEntityId")
    fun getAllTodoEntitiesForDayEntityId(dayEntityId: Long): LiveData<List<TodoEntity>>
     */

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    fun getNoteById(noteId: Long):Note?
    @Transaction
    @Query("SELECT * FROM events WHERE dayForeignId = :dayId")
    fun getEventsByDayId(dayId: Long): LiveData<List<EventEntity>>

    @Transaction
    @Query("SELECT * FROM todos WHERE dayForeignId = :dayId")
    fun getTodosByDayId(dayId: Long): LiveData<List<TodoEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewTodo(todo: TodoEntity)
    @Transaction
    @Query("SELECT todos.* FROM todos INNER JOIN day_data ON todos.dayForeignId = day_data.dayId WHERE day_data.date = :date")
    fun getTodosByDay(date: LocalDate): LiveData<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayEntity(dayEntity: DayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)

}