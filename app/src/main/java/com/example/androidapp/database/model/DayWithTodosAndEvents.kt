package com.example.androidapp.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class DayWithTodosAndEvents (
    @Embedded
    val dayEntity: DayEntity,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayForeignId"
    )
    var todos: List<TodoEntity>,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayForeignId"
    )
    var events: List<EventEntity>
)