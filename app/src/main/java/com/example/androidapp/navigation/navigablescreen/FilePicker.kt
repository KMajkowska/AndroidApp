package com.example.androidapp.navigation.navigablescreen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.example.androidapp.R
import com.example.androidapp.database.MyDatabaseConnection
import com.example.androidapp.database.databaseName
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.IOException

class FilePicker(
    private val mDayViewModel: DayViewModel,
    private val upPress: () -> Unit,
    private val isExport: Boolean,

    ) : NavigableScreen() {

    private val gson = Gson()

    private val days = "days"
    private val events = "events"
    private val notes = "notes"
    private val todos = "todos"

    @Composable
    override fun View() {
        val context = LocalContext.current
        val exportLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
                uri?.let {
                    performBackup(context, it)
                    upPress()
                } ?: run { upPress() }
            }
        val importLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    importDatabaseFromJson(context, it)
                    upPress()
                } ?: run { upPress() }
            }

        LaunchedEffect(Unit) {
            if (isExport) {
                exportLauncher.launch(null)
            } else {
                importLauncher.launch("application/json")
            }
        }

        BackHandler {
            upPress()
        }

    }

    private fun performBackup(context: Context, directoryUri: Uri) {
        val jsonObject = JSONObject()

        jsonObject.put(days, gson.toJson(mDayViewModel.allDayEntitiesSortedByDate.value ?: listOf<DayEntity>()))
        jsonObject.put(events, gson.toJson(mDayViewModel.allEventEntities.value ?: listOf<EventEntity>()))
        jsonObject.put(notes, gson.toJson(mDayViewModel.allNotes.value ?: listOf<Note>()))
        jsonObject.put(todos, gson.toJson(mDayViewModel.allTodoEntities.value ?: listOf<TodoEntity>()))

        val documentFile = DocumentFile.fromTreeUri(context, directoryUri)
        val resolver = context.contentResolver

        documentFile?.let { folder ->
            val newFile = folder.createFile("application/json", "${databaseName}_backup.json")
            newFile?.let { file ->
                try {
                    resolver.openOutputStream(file.uri)?.use { outputStream ->
                        outputStream.write(jsonObject.toString().toByteArray())
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun importDatabaseFromJson(context: Context, fileUri: Uri) {
        try {
            val jsonObject = JSONObject(readTextFromUri(context, fileUri))

            gson.fromJson<List<DayEntity>>(
                jsonObject.getString(days),
                object : TypeToken<List<DayEntity>>() {}.type
            ).forEach {
                it.dayId = null
                if (mDayViewModel.getInnerDayByDate(it.date) == null)
                    mDayViewModel.saveDayEntity(it)
            }

            gson.fromJson<List<EventEntity>>(
                jsonObject.getString(events),
                object : TypeToken<List<EventEntity>>() {}.type
            ).forEach {
                it.eventId = null
                mDayViewModel.saveEventEntity(it)
            }

            gson.fromJson<List<Note>>(
                jsonObject.getString(notes),
                object : TypeToken<List<Note>>() {}.type
            ).forEach {
                it.noteId = null
                mDayViewModel.addNewNote(it)
            }

            gson.fromJson<List<TodoEntity>>(
                jsonObject.getString(todos),
                object : TypeToken<List<TodoEntity>>() {}.type
            ).forEach {
                it.todoId = null
                mDayViewModel.saveTodoEntity(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readTextFromUri(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = inputStream.bufferedReader()
            val stringBuilder = StringBuilder()
            reader.useLines { lines ->
                lines.forEach { line ->
                    stringBuilder.append(line).append("\n")
                }
            }
            stringBuilder.toString()
        } ?: throw IOException("Unable to open URI: $uri")
    }
}