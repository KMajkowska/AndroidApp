package com.example.androidapp.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class DayWithEvents (
    @Embedded
    val dayEntity: DayEntity,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayForeignId"
    )
    val events: List<EventEntity>
)