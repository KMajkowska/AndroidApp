package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Query
import com.example.androidapp.database.model.savables.Savable

interface SavableDao {

    @Query("SELECT * FROM savables ORDER BY id DESC")
    fun getAllSavables(): LiveData<List<Savable>>

    @Delete
    suspend fun deleteSavable(savable: Savable)
}