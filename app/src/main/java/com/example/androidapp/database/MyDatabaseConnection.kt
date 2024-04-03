package com.example.androidapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidapp.database.converter.ColorConverter
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.database.dao.DayDao
import com.example.androidapp.database.dao.EventDao
import com.example.androidapp.database.dao.NoteDao
import com.example.androidapp.database.dao.TodoDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity

const val databaseName = "uniqrn_db"

@Database(
    entities = [DayEntity::class, TodoEntity::class, EventEntity::class, Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, ColorConverter::class)
abstract class MyDatabaseConnection : RoomDatabase() {
    abstract fun dayDao(): DayDao

    abstract fun eventDao(): EventDao

    abstract fun noteDao(): NoteDao

    abstract fun todoDao(): TodoDao


    companion object {
        @Volatile
        private var INSTANCE: MyDatabaseConnection? = null

        fun getDatabase(context: Context): MyDatabaseConnection =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MyDatabaseConnection::class.java,
                databaseName
            ).build()
    }
}
