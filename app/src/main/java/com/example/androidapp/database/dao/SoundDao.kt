package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidapp.database.model.savables.Sound

interface SoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewSound(sound: Sound)

    @Update
    suspend fun updateSound(sound: Sound)

    @Query("SELECT * FROM sounds ORDER BY id DESC")
    fun getAllSounds(): LiveData<List<Sound>>

    @Query("SELECT * FROM sounds WHERE id = :id")
    fun getSoundById(id: Long): Sound?
}