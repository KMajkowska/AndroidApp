package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.androidapp.database.model.EventEntity

@Dao
interface EventDao {
    @Upsert
    suspend fun saveEventEntity(event: EventEntity)

    @Query("SELECT * FROM events")
    fun getAllEventEntities(): LiveData<List<EventEntity>>

    @Delete
    suspend fun deleteEventEntity(event: EventEntity)

    @Transaction
    @Query("SELECT * FROM events WHERE dayForeignId = :dayId")
    fun getEventsByDayId(dayId: Long): LiveData<List<EventEntity>>
}