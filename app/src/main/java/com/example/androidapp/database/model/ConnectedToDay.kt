package com.example.androidapp.database.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

abstract class ConnectedToDay {
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
