package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val eventId: Long? = null,
    val dayForeignId: Long,
    val title: String = "",
    //val color: Color,
    //val icon: Icon,
    //val forDate: Long = System.currentTimeMillis()
)