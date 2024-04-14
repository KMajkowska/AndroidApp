package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidapp.database.model.ConnectedToNote

@Dao
interface ConnectedToNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConnectedToNote(connectedToNote: ConnectedToNote)

    @Update
    suspend fun updateConnectedToNote(connectedToNote: ConnectedToNote)

    @Query("SELECT * FROM connected_to_note ORDER BY id DESC")
    fun getAllConnectedToNote(): LiveData<List<ConnectedToNote>>

    @Query("SELECT * FROM connected_to_note WHERE id = :id")
    fun getConnectedToNoteById(id: Long): ConnectedToNote?

    @Query("SELECT * FROM connected_to_note WHERE noteForeignId= :noteForeignId ORDER BY id DESC")
    fun getAllConnectedToNoteByNoteId(noteForeignId: Long): LiveData<List<ConnectedToNote>>

    @Delete
    suspend fun deleteConnectedToNote(connectedToNote: ConnectedToNote)
}