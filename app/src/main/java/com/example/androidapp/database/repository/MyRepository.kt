package com.example.androidapp.database.repository

import androidx.lifecycle.LiveData
import com.example.androidapp.database.dao.ConnectedToNoteDao
import com.example.androidapp.database.dao.DayDao
import com.example.androidapp.database.dao.EventDao
import com.example.androidapp.database.dao.NoteDao
import com.example.androidapp.database.dao.TodoDao
import com.example.androidapp.database.model.ConnectedToNote
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import java.time.LocalDate

class MyRepository(
    private val dayDao: DayDao,
    private val eventDao: EventDao,
    private val noteDao: NoteDao,
    private val todoDao: TodoDao,
    private val connectedToNoteDao: ConnectedToNoteDao
) {

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> =
        dayDao.getAllDayEntitiesSortedByDate()

    val allTodoEntities: LiveData<List<TodoEntity>> = todoDao.getAllTodoEntities()

    val allEventEntities: LiveData<List<EventEntity>> = eventDao.getAllEventEntities()

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    val allConnectedToNotes: LiveData<List<ConnectedToNote>> = connectedToNoteDao.getAllConnectedToNote()

    val allDayEntitiesWithRelatedSortedByDate: LiveData<List<DayWithTodosAndEvents>> =
        dayDao.getAllDayEntitiesWithRelatedSortedByDate()

    fun getConnectedToNoteById(id: Long): ConnectedToNote? {
        return connectedToNoteDao.getConnectedToNoteById(id)
    }

    suspend fun updateConnectedToNote(connectedToNote: ConnectedToNote) {
        connectedToNoteDao.updateConnectedToNote(connectedToNote)
    }

    suspend fun addNewConnectedToNote(connectedToNote: ConnectedToNote) {
        connectedToNoteDao.addConnectedToNote(connectedToNote)
    }

    suspend fun deleteConnectedToNote(connectedToNote: ConnectedToNote) {
        connectedToNoteDao.deleteConnectedToNote(connectedToNote)
    }

    suspend fun deleteNoteEntity(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun saveDayEntity(newDayEntity: DayEntity) {
        dayDao.saveDayEntity(newDayEntity)
    }

    suspend fun deleteDayEntity(dayEntity: DayEntity) {
        dayDao.deleteDayEntity(dayEntity)
    }

    suspend fun saveTodoEntity(newTodoEntity: TodoEntity) {
        todoDao.saveTodoEntity(newTodoEntity)
    }

    suspend fun saveEventEntity(newEventEntity: EventEntity) {
        eventDao.saveEventEntity(newEventEntity)
    }

    suspend fun addNewNote(note: Note): Long {
        return noteDao.addNewNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }

    suspend fun changeNoteTitle(noteId: Long, newTitle: String) {
        noteDao.changeNoteTitle(noteId, newTitle)
    }

    fun getNoteByDate(date: LocalDate): Note? {
        return noteDao.getNoteByDate(date)
    }

    fun getDayByDate(date: LocalDate): DayEntity? {
        return dayDao.getDayByDate(date)
    }

    fun getConnectedToNoteByNoteId(noteId: Long): LiveData<List<ConnectedToNote>> {
        return connectedToNoteDao.getAllConnectedToNoteByNoteId(noteId)
    }

    fun getEventsByDayId(dayId: Long): LiveData<List<EventEntity>> {
        return eventDao.getEventsByDayId(dayId)
    }

    fun getTodosByDayId(dayId: Long): LiveData<List<TodoEntity>> {
        return todoDao.getTodosByDayId(dayId)
    }

    fun getDayIdWithRelated(dayId: Long): DayWithTodosAndEvents? {
        return dayDao.getDayIdWithRelated(dayId)
    }

    suspend fun getDayIdWithRelatedByDate(date: LocalDate): DayWithTodosAndEvents? {
        return dayDao.getDayIdWithRelatedByDate(date)
    }

    suspend fun deleteTodoEntity(todo: TodoEntity) {
        todoDao.deleteTodoEntity(todo)
    }

    suspend fun deleteEventEntity(event: EventEntity) {
        eventDao.deleteEventEntity(event)
    }


}