package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.androidapp.database.model.savables.Note
import java.time.LocalDate

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE noteDate = :date")
    fun getNoteByDate(date: LocalDate): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: Long): Note?
}