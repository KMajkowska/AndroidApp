package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.time.LocalDate

@Entity(tableName = "day_data", indices = [Index(value = ["date"], unique = true)])
data class DayEntity (
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var dayId: Long? = null,
    var dayTitle: String = "",
    val date: LocalDate
)