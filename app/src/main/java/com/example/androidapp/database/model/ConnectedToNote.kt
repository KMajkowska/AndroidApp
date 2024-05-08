package com.example.androidapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.io.File

@Entity(tableName = "connected_to_note")
class ConnectedToNote(
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var noteForeignId: Long,
    var contentOrPath: String,
    val mimeType: String
) {
    fun doBeforeDeletingRecord() {
        if (contentOrPath.isBlank() || mimeType.isBlank())
            return

        val file = File(contentOrPath)
        if (file.exists())
            file.delete()
    }
}
