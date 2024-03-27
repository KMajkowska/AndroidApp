package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidapp.database.model.savables.Video

interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewVideo(video: Video)

    @Update
    suspend fun updateVideo(video: Video)

    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): LiveData<List<Video>>

    @Query("SELECT * FROM videos WHERE id = :id")
    fun getVideoById(id: Long): Video?
}