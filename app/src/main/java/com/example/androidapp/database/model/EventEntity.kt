package com.example.androidapp.database.model

import android.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val eventId: Long? = null,
    val dayForeignId: Long,
    val title: String = "",
    val colorInt: Color = Color.valueOf(0),
   // val icon: ImageVector,
)