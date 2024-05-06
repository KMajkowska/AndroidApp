package com.example.androidapp.database.model

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.androidapp.R
import com.google.gson.annotations.Expose
import java.time.LocalDate

@Entity(tableName = "day_data", indices = [Index(value = ["date"], unique = true)])
data class DayEntity (
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var dayId: Long? = null,
    var dayTitle: String = "",
    val date: LocalDate
){
    fun getDayTitleIfSet(context: Context): String {
        return dayTitle.ifBlank { context.resources.getString(R.string.empty_title) }
    }
}