package com.example.androidapp.database.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long?=0,
    @ColumnInfo(name="title")
    val noteTitle: String = "",
    @ColumnInfo(name="content")
    val content: String = "",
    val date:String = "",
    val pinned: Boolean = false

)
