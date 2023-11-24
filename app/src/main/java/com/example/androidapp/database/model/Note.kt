package com.example.androidapp.database.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long=0,
    val noteTitle: String = "",
    val content: String = "",
    val date:String = "",
    val pinned: Boolean = false

)
