package com.example.androidapp.database.model.savables

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.io.File

@Entity(tableName = "savables" )
abstract class Savable {
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    val path: String = ""

    fun doBeforeDeletingRecord() {
        if (path.isBlank())
            return

        val file = File(path)
        if (file.exists())
            file.delete()
    }
}