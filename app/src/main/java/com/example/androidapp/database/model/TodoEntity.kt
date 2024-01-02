package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "todos")
data class TodoEntity(
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var todoId: Long? = null,
    var dayForeignId: Long,
    var title: String = "",
    var isDone: Boolean = false,
)