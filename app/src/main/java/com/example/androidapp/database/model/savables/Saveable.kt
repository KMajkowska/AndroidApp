package com.example.androidapp.database.model.savables

import com.example.androidapp.database.model.ConnectedToDay
import java.io.File

abstract class Saveable : ConnectedToDay() {
    var path: String = ""
    var dayForeignId: Long = -1

    fun doBeforeDeletingRecord() {
        if (path.isBlank())
            return

        val file = File(path)
        if (file.exists())
            file.delete()
    }
}