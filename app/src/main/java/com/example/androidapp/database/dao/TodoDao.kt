package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.androidapp.database.model.TodoEntity
import java.time.LocalDate

@Dao
interface TodoDao {
    @Upsert
    suspend fun saveTodoEntity(todo: TodoEntity)

    @Delete
    suspend fun deleteTodoEntity(todo: TodoEntity)

    @Transaction
    @Query("SELECT * FROM todos")
    fun getAllTodoEntities(): LiveData<List<TodoEntity>>

    @Transaction
    @Query("SELECT * FROM todos WHERE dayForeignId = :dayId")
    fun getTodosByDayId(dayId: Long): LiveData<List<TodoEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewTodo(todo: TodoEntity)
    @Transaction
    @Query("SELECT todos.* FROM todos INNER JOIN day_data ON todos.dayForeignId = day_data.dayId WHERE day_data.date = :date")
    fun getTodosByDay(date: LocalDate): LiveData<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)
}