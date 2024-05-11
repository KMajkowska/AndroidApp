package com.example.androidapp.database.model


import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidapp.R
import com.google.gson.annotations.Expose
import java.time.LocalDate

@Entity(tableName = "notes")
data class Note (
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var noteTitle: String = "",
    val content: String = "",
    var noteDate: LocalDate? = null,
    val pinned: Boolean = false,
    var noteImageUri: String? = null

) {
    fun getNoteTitleIfSet(context: Context): String {
        return noteTitle.ifBlank { context.resources.getString(R.string.empty_title) }
    }
}
