package com.example.androidapp.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class DayWithTodos (
    @Embedded
    val dayEntity: DayEntity,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayForeignId"
    )
    val todos: List<TodoEntity>
)