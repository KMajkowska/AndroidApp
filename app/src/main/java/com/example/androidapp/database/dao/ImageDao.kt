package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidapp.database.model.savables.Image

interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewImage(image: Image)

    @Update
    suspend fun updateImage(image: Image)

    @Query("SELECT * FROM images ORDER BY id DESC")
    fun getAllImages(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE id = :id")
    fun getImageById(id: Long): Image?
}