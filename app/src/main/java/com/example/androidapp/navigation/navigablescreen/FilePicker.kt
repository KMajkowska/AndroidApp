package com.example.androidapp.navigation.navigablescreen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.example.androidapp.database.databaseName
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.IOException

const val DAYS = "days"
const val EVENTS = "events"
const val NOTES = "notes"
const val TODOS = "todos"
const val CONNECTED_TO_NOTE = "connectedToNote"

class FilePicker(
    private val mDayViewModel: DayViewModel,
    private val upPress: () -> Unit,
    private val isExport: Boolean
    ) : NavigableScreen() {

    private val gson = Gson()

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

        BackHandler { upPress() }
    }

    fun performBackup(context: Context, directoryUri: Uri) {
        val jsonObject = JSONObject()

        jsonObject.put(DAYS, gson.toJson(mDayViewModel.allDayEntitiesSortedByDate.value ?: listOf<DayEntity>()))
        jsonObject.put(EVENTS, gson.toJson(mDayViewModel.allEventEntities.value ?: listOf<EventEntity>()))
        jsonObject.put(NOTES, gson.toJson(mDayViewModel.allNotes.value ?: listOf<Note>()))
        jsonObject.put(TODOS, gson.toJson(mDayViewModel.allTodoEntities.value ?: listOf<TodoEntity>()))
        jsonObject.put(CONNECTED_TO_NOTE, gson.toJson(mDayViewModel.allConnectedToNote.value ?: listOf<ConnectedToNote>()))

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

    fun importDatabaseFromJson(context: Context, fileUri: Uri) {
        try {
            val jsonObject = JSONObject(readTextFromUri(context, fileUri))

            gson.fromJson<List<DayEntity>>(
                jsonObject.getString(DAYS),
                object : TypeToken<List<DayEntity>>() {}.type
            ).forEach {
                it.dayId = null
                if (mDayViewModel.getInnerDayByDate(it.date) == null)
                    mDayViewModel.saveDayEntity(it)
            }

            gson.fromJson<List<EventEntity>>(
                jsonObject.getString(EVENTS),
                object : TypeToken<List<EventEntity>>() {}.type
            ).forEach {
                it.eventId = null
                mDayViewModel.saveEventEntity(it)
            }

            gson.fromJson<List<Note>>(
                jsonObject.getString(NOTES),
                object : TypeToken<List<Note>>() {}.type
            ).forEach {
                it.id = null
                mDayViewModel.addNewNote(it)
            }

            gson.fromJson<List<TodoEntity>>(
                jsonObject.getString(TODOS),
                object : TypeToken<List<TodoEntity>>() {}.type
            ).forEach {
                it.todoId = null
                mDayViewModel.saveTodoEntity(it)
            }

            // if it is a file path then it may not exist! How to fix this?
            gson.fromJson<List<ConnectedToNote>>(
                jsonObject.getString(CONNECTED_TO_NOTE),
                object : TypeToken<List<ConnectedToNote>>() {}.type
            ).forEach {
                it.id = null
                mDayViewModel.addConnectedToNote(it)
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