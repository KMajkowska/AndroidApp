package com.example.androidapp.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// when adding notes from backup please make sure that note is appended! not replaced !
@Entity(tableName = "notes", indices = [Index(value = ["forDate"], unique = true)])
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val forDate: Long = System.currentTimeMillis()
)