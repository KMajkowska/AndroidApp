package com.example.androidapp.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "day_data", indices = [Index(value = ["date"], unique = true)])
data class DayEntity (
    @PrimaryKey(autoGenerate = true)
    val dayId: Long = 0,
    @Embedded
    val note: Note = Note(),
    val date: String = LocalDate.now().toString()
)