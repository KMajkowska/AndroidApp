package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val todoId: Long? = null,
    var dayForeignId: Long,
    var title: String = "",
    var isDone: Boolean = false,
)