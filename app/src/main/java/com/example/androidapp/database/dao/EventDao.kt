package com.example.androidapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.androidapp.database.entity.EventEntity

@Dao
interface EventDao {
    @Insert
    fun insert(event: EventEntity)

    @Query("SELECT * FROM events")
    fun getAll(): List<EventEntity>

    @Query("SELECT * FROM events WHERE forDate = :date")
    fun getAllTodosForDate(date: Long): List<EventEntity>
}