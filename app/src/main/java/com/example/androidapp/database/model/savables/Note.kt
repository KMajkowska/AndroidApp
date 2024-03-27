package com.example.androidapp.database.model.savables


import androidx.room.Entity

import java.time.LocalDate

@Entity(tableName = "notes")
data class Note(
    var noteTitle: String = "",
    val content: String = "",
    var noteDate:LocalDate? = null,
    val pinned: Boolean = false,
) : Savable()