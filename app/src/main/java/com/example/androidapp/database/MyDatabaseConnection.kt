package com.example.androidapp.database

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidapp.database.converter.ColorConverter
import com.example.androidapp.database.converter.LocalDateConverter
import com.example.androidapp.database.dao.MyDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import java.io.IOException

const val databaseName = "uniqrn_db"
fun performBackup(context: Context, directoryUri: Uri) {
    val resolver = context.contentResolver
    val documentFile = DocumentFile.fromTreeUri(context, directoryUri)

    documentFile?.let { folder ->
        val newFile = folder.createFile("application/vnd.sqlite3", "${databaseName}_backup.sqlite")
        newFile?.let { file ->
            try {
                resolver.openOutputStream(file.uri)?.use { outputStream ->
                    val dbFile = context.getDatabasePath(databaseName)
                    dbFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                // Notify user of success, possibly through a toast or a dialog
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle exceptions, notify user of failure
            }
        }
    }
}

fun importDatabase(context: Context, uri: Uri) {
    try {
        val dbFile = context.getDatabasePath(databaseName)

        MyDatabaseConnection.invalidate()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            if (dbFile.exists())
                dbFile.delete()

            dbFile.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        }

        MyDatabaseConnection.openDatabase()

    } catch (e: IOException) {
        e.printStackTrace()
    }
}


@Database(
    entities = [DayEntity::class, TodoEntity::class, EventEntity::class, Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, ColorConverter::class)
abstract class MyDatabaseConnection : RoomDatabase() {
    abstract fun myDao(): MyDao

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

        fun invalidate() {
            if (INSTANCE != null) {
                if (INSTANCE?.isOpen == true)
                    INSTANCE?.openHelper?.close()

                INSTANCE = null;
            }

        }

        fun openDatabase() {
            if (INSTANCE?.isOpen == false)
                INSTANCE?.openHelper?.writableDatabase
        }
    }
}
