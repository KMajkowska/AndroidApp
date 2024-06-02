package com.example.androidapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.media.MimeTypeEnum
import com.example.androidapp.navigation.navigablescreen.CONNECTED_TO_NOTE
import com.example.androidapp.navigation.navigablescreen.DAYS
import com.example.androidapp.navigation.navigablescreen.EVENTS
import com.example.androidapp.navigation.navigablescreen.FilePicker
import com.example.androidapp.navigation.navigablescreen.NOTES
import com.example.androidapp.navigation.navigablescreen.TODOS
import com.google.gson.Gson
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FilePickerTest {

    private lateinit var context: Context
    private lateinit var dayViewModel: DayViewModel
    private lateinit var filePicker: FilePicker
    private lateinit var documentFile: DocumentFile
    private lateinit var resolver: ContentResolver

    private val gson = Gson()

    private fun getEmptyBackup(): JSONObject {
        return JSONObject().apply {
            put(DAYS, emptyList<DayEntity>())
            put(EVENTS, emptyList<EventEntity>())
            put(NOTES, emptyList<Note>())
            put(TODOS, emptyList<TodoEntity>())
            put(CONNECTED_TO_NOTE, emptyList<ConnectedToNote>())
        }
    }

    private fun putDay(jsonObject: JSONObject): JSONObject {
        return jsonObject.put(
            DAYS, gson.toJson(listOf(DayEntity(dayId = 0, date = LocalDate.now())))
        )
    }

    private fun putEvent(jsonObject: JSONObject): JSONObject {
        return jsonObject.put(EVENTS, gson.toJson(listOf(EventEntity(dayForeignId = 0))))
    }

    private fun putNotes(jsonObject: JSONObject): JSONObject {
        return jsonObject.put(NOTES, gson.toJson(listOf(Note(id = 1))))
    }

    private fun putTodos(jsonObject: JSONObject): JSONObject {
        return jsonObject.put(TODOS, gson.toJson(listOf(TodoEntity(dayForeignId = 0))))
    }

    private fun putConnectedToNote(jsonObject: JSONObject): JSONObject {
        return jsonObject.put(
            CONNECTED_TO_NOTE,
            gson.toJson(
                listOf(
                    ConnectedToNote(
                        noteForeignId = 1,
                        contentOrPath = "empty",
                        mimeType = MimeTypeEnum.IMAGE.toString()
                    )
                )
            )
        )
    }

    private fun importJSONObjectToMockedDatabase(jsonObject: JSONObject) {
        val json = jsonObject.toString()
        val fileUri = mockk<Uri>()

        every { context.contentResolver.openInputStream(fileUri) } returns json.byteInputStream()

        filePicker.importDatabaseFromJson(context, fileUri)
    }

    @Before
    fun setup() {
        context = mockk()
        dayViewModel = mockk()
        documentFile = mockk()
        resolver = mockk()

        every { context.contentResolver } returns resolver

        filePicker = FilePicker(dayViewModel, {}, true)

        every { dayViewModel.getInnerDayByDate(any()) } returns null
        every { dayViewModel.saveDayEntity(any()) } just Runs
        every { dayViewModel.saveEventEntity(any()) } just Runs
        every { dayViewModel.addNewNote(any()) } returns 0
        every { dayViewModel.saveTodoEntity(any()) } just Runs
        every { dayViewModel.addConnectedToNote(any()) } just Runs
    }

    @Test
    fun testImportDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putDay(this)
            putEvent(this)
            putNotes(this)
            putTodos(this)
            putConnectedToNote(this)
        })

        verify {
            dayViewModel.saveDayEntity(any())
            dayViewModel.saveEventEntity(any())
            dayViewModel.addNewNote(any())
            dayViewModel.saveTodoEntity(any())
            dayViewModel.addConnectedToNote(any())
        }
    }

    @Test
    fun testImportDayDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putDay(this)
        })

        verify { dayViewModel.saveDayEntity(any()) }

        verify(exactly = 0) {
            dayViewModel.saveEventEntity(any())
            dayViewModel.addNewNote(any())
            dayViewModel.saveTodoEntity(any())
            dayViewModel.addConnectedToNote(any())
        }
    }

    @Test
    fun testImportEventDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putEvent(this)
        })

        verify { dayViewModel.saveEventEntity(any()) }

        verify(exactly = 0) {
            dayViewModel.saveDayEntity(any())
            dayViewModel.addNewNote(any())
            dayViewModel.saveTodoEntity(any())
            dayViewModel.addConnectedToNote(any())
        }
    }

    @Test
    fun testImportNoteDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putNotes(this)
        })

        verify { dayViewModel.addNewNote(any()) }

        verify(exactly = 0) {
            dayViewModel.saveDayEntity(any())
            dayViewModel.saveEventEntity(any())
            dayViewModel.saveTodoEntity(any())
            dayViewModel.addConnectedToNote(any())
        }
    }

    @Test
    fun testImportTodoDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putTodos(this)
        })

        verify { dayViewModel.saveTodoEntity(any()) }

        verify(exactly = 0) {
            dayViewModel.saveDayEntity(any())
            dayViewModel.saveEventEntity(any())
            dayViewModel.addNewNote(any())
            dayViewModel.addConnectedToNote(any())
        }
    }

    @Test
    fun testImportConnectedToNoteDatabaseFromJson() {
        importJSONObjectToMockedDatabase(getEmptyBackup().apply {
            putConnectedToNote(this)
        })

        verify { dayViewModel.addConnectedToNote(any()) }

        verify(exactly = 0) {
            dayViewModel.saveDayEntity(any())
            dayViewModel.saveEventEntity(any())
            dayViewModel.addNewNote(any())
            dayViewModel.saveTodoEntity(any())
        }
    }
}
