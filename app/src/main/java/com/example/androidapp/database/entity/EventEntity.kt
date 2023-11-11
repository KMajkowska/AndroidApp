package com.example.androidapp.database.entity

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val color: Color,
    val icon: Icon,
    val forDate: Long = System.currentTimeMillis()
)