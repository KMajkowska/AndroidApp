package com.example.androidapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.androidapp.database.entity.TodoEntity

@Dao
interface TodoDao {
    @Insert
    fun insert(todo: TodoEntity)

    @Query("SELECT * FROM todos")
    fun getAll(): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE forDate = :date")
    fun getAllTodosForDate(date: Long): List<TodoEntity>
}