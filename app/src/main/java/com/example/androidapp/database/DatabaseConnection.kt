package com.example.androidapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity

@Database(
    entities = [DayEntity::class, TodoEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DatabaseConnection : RoomDatabase() {
    abstract fun myDao(): MyDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseConnection? = null

        fun getDatabase(context: Context): DatabaseConnection {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseConnection::class.java,
                    "uniqrn_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}