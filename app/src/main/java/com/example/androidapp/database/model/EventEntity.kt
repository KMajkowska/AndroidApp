package com.example.androidapp.database.model

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val eventId: Long? = null,
    val dayForeignId: Long,
    val title: String = "",
    val color: Color = Color.valueOf(0),
    val category: String = "General"
   // val icon: ImageVector,
)