package com.example.androidapp.database.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.time.LocalDate

@Entity(tableName = "notes")
data class Note(
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var noteId: Long? = null,
    val noteTitle: String = "",
    val content: String = "",
    var noteDate:LocalDate? = null,
    val pinned: Boolean = false

)