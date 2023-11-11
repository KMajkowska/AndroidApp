package com.example.androidapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidapp.database.dao.EventDao
import com.example.androidapp.database.dao.NoteDao
import com.example.androidapp.database.dao.TodoDao
import com.example.androidapp.database.entity.EventEntity
import com.example.androidapp.database.entity.NoteEntity
import com.example.androidapp.database.entity.TodoEntity

@Database(
    entities = [EventEntity::class, NoteEntity::class, TodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun noteDao(): NoteDao

    abstract fun TodoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uniqrn_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}