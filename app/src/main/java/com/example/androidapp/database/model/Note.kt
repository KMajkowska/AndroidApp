package com.example.androidapp.database.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName="notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long=0,
    val noteTitle: String = "",
    val content: String = "",
    var noteDate:LocalDate? = null,
    val pinned: Boolean = false

)
