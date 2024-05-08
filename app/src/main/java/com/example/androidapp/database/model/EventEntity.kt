package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "events")
data class EventEntity(
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var eventId: Long? = null,
    val dayForeignId: Long,
    val title: String = "",
    val category: String = "General"
)