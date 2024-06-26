package com.example.androidapp

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidapp.database.MyDatabaseConnection
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.viewmodel.DayViewModel
import com.example.androidapp.media.MimeTypeEnum
import com.example.androidapp.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DayViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dayViewModel: DayViewModel
    private lateinit var database: MyDatabaseConnection
    private lateinit var application: Application

    /*-------------------- begin::configure --------------------*/
    private fun createNewDatabaseInstance() : MyDatabaseConnection {
        return inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabaseConnection::class.java
        ).allowMainThreadQueries().build()
    }
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        application = ApplicationProvider.getApplicationContext()
        database = createNewDatabaseInstance()
        val notificationHelper = NotificationHelper(application, true)
        dayViewModel = DayViewModel(application, notificationHelper, database)
    }

    @After
    fun tearDown() = runTest {
        database.clearAllTables()
        database.close()
        waitForCoroutinesToFinish()
        database = createNewDatabaseInstance()
    }

    private fun setUpDayEntity() : DayEntity  = runBlocking {
        val testDate = LocalDate.now()
        val dayEntity = DayEntity(date = testDate)

        dayViewModel.saveDayEntity(dayEntity)
        return@runBlocking dayViewModel.getDayByDate(testDate)
    }

    private fun waitForCoroutinesToFinish() = runBlocking {
        dayViewModel.viewModelScope.coroutineContext[Job]?.children?.forEach { it.join() }
    }

    /*-------------------- end::configure --------------------*/

    /*-------------------- begin::day entity tests --------------------*/
    @Test
    fun testSaveAndRetrieveDayEntity() = runBlocking {
        val testDate = LocalDate.now()
        val dayEntity = DayEntity(date = testDate)

        dayViewModel.saveDayEntity(dayEntity)

        val retrievedDayEntity = dayViewModel.getDayByDate(testDate)

        assertEquals(dayEntity.date, retrievedDayEntity.date)
        assertNull(dayEntity.dayId)
        assertNotNull(retrievedDayEntity.dayId)
    }

    @Test
    fun testDeleteDayEntity() = runBlocking {
        val testDate = LocalDate.now()
        val dayEntity = DayEntity(date = testDate)

        dayViewModel.saveDayEntity(dayEntity)
        val savedDayEntity = dayViewModel.getDayByDate(testDate)

        assertEquals(dayEntity.date, savedDayEntity.date)
        assertNull(dayEntity.dayId)
        assertNotNull(savedDayEntity.dayId)

        dayViewModel.deleteDayEntity(savedDayEntity)
        waitForCoroutinesToFinish()
        assertNull(dayViewModel.getInnerDayByDate(testDate))
    }

    @Test
    fun testDeleteEmptyDayEntityWhileNotEmpty() = runBlocking {
        val testDate = LocalDate.now()
        val dayEntity = DayEntity(date = testDate, dayTitle = "SOME TITLE")

        dayViewModel.saveDayEntity(dayEntity)
        val savedDayEntity = dayViewModel.getDayByDate(testDate)

        assertEquals(dayEntity.date, savedDayEntity.date)
        assertNull(dayEntity.dayId)
        assertNotNull(savedDayEntity.dayId)

        dayViewModel.deleteDayEntityIfEmpty(savedDayEntity.dayId!!)

        assertNotNull(dayViewModel.getInnerDayByDate(testDate))
    }

    @Test
    fun testDeleteEmptyDayEntityWhileEmpty() = runBlocking {
        val testDate = LocalDate.now()
        val dayEntity = DayEntity(date = testDate)

        dayViewModel.saveDayEntity(dayEntity)
        waitForCoroutinesToFinish()
        val savedDayEntity = dayViewModel.getDayByDate(testDate)

        assertEquals(dayEntity.date, savedDayEntity.date)
        assertNull(dayEntity.dayId)
        assertNotNull(savedDayEntity.dayId)

        dayViewModel.deleteDayEntityIfEmpty(savedDayEntity.dayId!!)
        waitForCoroutinesToFinish()

        assertNull(dayViewModel.getInnerDayByDate(testDate))
    }

    @Test
    fun testGetByDateCreatesNewEntity() = runBlocking {
        val testDate = LocalDate.now()

        val savedDayEntity = dayViewModel.getDayByDate(testDate)
        assertNotNull(savedDayEntity.dayId)
        assertEquals(testDate, savedDayEntity.date)
    }
    /*-------------------- end::day entity tests --------------------*/

    /*-------------------- begin::note entity tests --------------------*/
    @Test
    fun testAddNewNote() = runBlocking {
        val noteTitle = "Some title"
        val testDate = LocalDate.now()

        val note = Note(noteTitle = noteTitle, noteDate = testDate)

        dayViewModel.addNewNote(note)

        val retrievedNote = dayViewModel.getNoteByDate(testDate)
        assertNotNull(retrievedNote)
        assertEquals(retrievedNote!!.noteTitle, noteTitle)
        assertEquals(retrievedNote.noteTitle, noteTitle)
    }

    @Test
    fun testUpdateNote() = runBlocking {
        val noteTitle = "Some title"
        val newNoteTitle = "Some other title"
        val testDate = LocalDate.now()

        val note = Note(noteTitle = noteTitle, noteDate = testDate)

        dayViewModel.addNewNote(note)
        waitForCoroutinesToFinish()
        val retrievedNote = dayViewModel.getNoteByDate(testDate)
        assertNotNull(retrievedNote)

        retrievedNote!!.noteTitle = newNoteTitle
        dayViewModel.updateNote(retrievedNote)
        waitForCoroutinesToFinish()
        val retrievedNoteAfterUpdate = dayViewModel.getNoteByDate(testDate)
        waitForCoroutinesToFinish()
        assertNotNull(retrievedNoteAfterUpdate)

        assertEquals(retrievedNoteAfterUpdate!!.noteTitle, newNoteTitle)
        assertEquals(retrievedNoteAfterUpdate.id, retrievedNote.id)
    }

    @Test
    fun testAllNotesList() = runBlocking {
        val note = Note(noteTitle = "Some title")

        dayViewModel.addNewNote(note)

        val retrievedNote = dayViewModel.allNotes.getOrAwaitValue()

        assertNotNull(retrievedNote)
        assertTrue(retrievedNote.size == 1)

        assertEquals(retrievedNote[0].noteTitle, note.noteTitle)
        assertNotEquals(retrievedNote[0].id, note.id)
    }

    @Test
    fun testGetNoteByIdAndDate() = runBlocking {
        val testDate = LocalDate.now()

        val note = Note(noteDate = testDate)

        dayViewModel.addNewNote(note)

        val retrievedNote = dayViewModel.getNoteByDate(testDate)
        assertNotNull(retrievedNote)
        assertNotNull(retrievedNote!!.id)
        assertEquals(retrievedNote, dayViewModel.getNoteById(retrievedNote.id!!))
    }

    /*-------------------- end::note entity tests --------------------*/

    /*-------------------- begin::to do entity tests --------------------*/
    @Test
    fun testGetTodosByDayId() = runBlocking {
        val dayEntity = setUpDayEntity()
        val todoEntity = TodoEntity(dayForeignId = dayEntity.dayId!!, title = "Some todo")
        dayViewModel.saveTodoEntity(todoEntity)

        val todos = dayViewModel.getTodosByDayId(dayEntity.dayId!!).getOrAwaitValue()
        assertNotNull(todos)
        assertTrue(todos.size == 1)
        assertEquals(todos[0].title, todoEntity.title)
    }

    @Test
    fun testAllTodoEntitiesWithDelete() = runBlocking {
        val dayEntity = setUpDayEntity()
        val todoEntity0 = TodoEntity(dayForeignId = dayEntity.dayId!!, title = "0")
        val todoEntity1 = TodoEntity(dayForeignId = dayEntity.dayId!!, title = "1")

        dayViewModel.saveTodoEntity(todoEntity0)
        dayViewModel.saveTodoEntity(todoEntity1)
        waitForCoroutinesToFinish()

        val todos = dayViewModel.allTodoEntities.getOrAwaitValue()
        assertNotNull(todos)
        assertTrue(todos.size == 2)
        dayViewModel.deleteTodoEntity(todos[1])
        waitForCoroutinesToFinish()

        val todosAfterDelete = dayViewModel.allTodoEntities.getOrAwaitValue()
        assertNotNull(todosAfterDelete)
        assertTrue(todosAfterDelete.size == 1)
        assertEquals(todosAfterDelete[0], todos[0])
    }
    /*-------------------- end::to do entity tests --------------------*/

    /*-------------------- begin::to do entity tests --------------------*/
    @Test
    fun testGetEventsByDayId() = runBlocking {
        val dayEntity = setUpDayEntity()
        val eventEntity = EventEntity(dayForeignId = dayEntity.dayId!!, title = "Some todo")
        dayViewModel.saveEventEntity(eventEntity)

        val events = dayViewModel.getEventsByDayId(dayEntity.dayId!!).getOrAwaitValue()
        assertNotNull(events)
        assertTrue(events.size == 1)
        assertEquals(events[0].title, eventEntity.title)
    }

    @Test
    fun testAllEventEntitiesWithDelete() = runBlocking {
        val dayEntity = setUpDayEntity()
        val eventEntity0 = EventEntity(dayForeignId = dayEntity.dayId!!, title = "0")
        val eventEntity1 = EventEntity(dayForeignId = dayEntity.dayId!!, title = "1")

        dayViewModel.saveEventEntity(eventEntity0)
        dayViewModel.saveEventEntity(eventEntity1)
        waitForCoroutinesToFinish()

        val events = dayViewModel.allEventEntities.getOrAwaitValue()
        assertNotNull(events)
        assertTrue(events.size == 2)
        dayViewModel.deleteEventEntity(events[1])
        waitForCoroutinesToFinish()

        val eventsAfterDelete = dayViewModel.allEventEntities.getOrAwaitValue()
        assertNotNull(eventsAfterDelete)
        assertTrue(eventsAfterDelete.size == 1)
        assertEquals(eventsAfterDelete[0], events[0])
    }
    /*-------------------- end::to do entity tests --------------------*/

    /*-------------------- begin::connected to note entity tests --------------------*/
    @Test
    fun testDeleteConnectedToNoteEntity() = runBlocking {
        val connectedToNote = ConnectedToNote(noteForeignId = 0, contentOrPath = "0", mimeType = MimeTypeEnum.IMAGE.toString())

        dayViewModel.addConnectedToNote(connectedToNote)
        val savedConnectedToNotes = dayViewModel.getConnectedToNoteByNoteId(0).getOrAwaitValue()

        assertEquals(savedConnectedToNotes.size, 1)

        val savedConnectedToNote = savedConnectedToNotes[0]
        assertNotNull(savedConnectedToNote)

        assertEquals(connectedToNote.noteForeignId, savedConnectedToNote.noteForeignId)
        assertEquals(connectedToNote.mimeType, savedConnectedToNote.mimeType)
        assertEquals(connectedToNote.contentOrPath, savedConnectedToNote.contentOrPath)

        dayViewModel.deleteConnectedToNote(savedConnectedToNote)
        waitForCoroutinesToFinish()
        assertTrue(dayViewModel.getConnectedToNoteByNoteId(0).getOrAwaitValue().isEmpty())
        assertTrue(dayViewModel.allConnectedToNote.getOrAwaitValue().isEmpty())
    }
    /*-------------------- end::connected to note entity tests --------------------*/
}