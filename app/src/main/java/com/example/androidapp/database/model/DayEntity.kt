package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "day_data", indices = [Index(value = ["date"], unique = true)])
data class DayEntity (
    @PrimaryKey(autoGenerate = true)
    val dayId: Long? = null,
    var dayTitle: String = "",
    val date: LocalDate
)