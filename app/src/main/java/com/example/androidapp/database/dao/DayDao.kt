package com.example.androidapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import java.time.LocalDate

@Dao
interface DayDao {
    @Upsert
    suspend fun saveDayEntity(dayEntity: DayEntity)

    @Delete
    suspend fun deleteDayEntity(dayEntity: DayEntity)

    @Transaction
    @Query("SELECT * FROM day_data ORDER BY date ASC")
    fun getAllDayEntitiesSortedByDate(): LiveData<List<DayEntity>>

    @Query("SELECT * FROM day_data")
    fun getAllDayEntities(): LiveData<List<DayEntity>>

    @Transaction
    @Query("SELECT * FROM day_data WHERE date = :date")
    fun getDayByDate(date: LocalDate): DayEntity?

    @Transaction
    @Query("SELECT * FROM day_data WHERE dayId = :dayId")
    fun getDayIdWithRelated(dayId: Long): DayWithTodosAndEvents?

    @Transaction
    @Query("SELECT * FROM day_data WHERE date = :date")
    suspend fun getDayIdWithRelatedByDate(date: LocalDate): DayWithTodosAndEvents?

    @Transaction
    @Query("SELECT * FROM day_data ORDER BY date ASC")
    fun getAllDayEntitiesWithRelatedSortedByDate(): LiveData<List<DayWithTodosAndEvents>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayEntity(dayEntity: DayEntity): Long
}