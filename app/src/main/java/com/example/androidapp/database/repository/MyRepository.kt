package com.example.androidapp.database.repository

import androidx.lifecycle.LiveData
import com.example.androidapp.database.dao.DayDao
import com.example.androidapp.database.dao.EventDao
import com.example.androidapp.database.dao.ImageDao
import com.example.androidapp.database.dao.NoteDao
import com.example.androidapp.database.dao.SoundDao
import com.example.androidapp.database.dao.TodoDao
import com.example.androidapp.database.dao.VideoDao
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.model.savables.Image
import com.example.androidapp.database.model.savables.Sound
import com.example.androidapp.database.model.savables.Video
import java.time.LocalDate

class MyRepository(
    private val dayDao: DayDao,
    private val eventDao: EventDao,
    private val noteDao: NoteDao,
    private val todoDao: TodoDao,
    private val imageDao: ImageDao,
    private val soundDao: SoundDao,
    private val videoDao: VideoDao
) {

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> =
        dayDao.getAllDayEntitiesSortedByDate()

    val allTodoEntities: LiveData<List<TodoEntity>> = todoDao.getAllTodoEntities()

    val allEventEntities: LiveData<List<EventEntity>> = eventDao.getAllEventEntities()

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    val allImages: LiveData<List<Image>> = imageDao.getAllImages()

    val allSounds: LiveData<List<Sound>> = soundDao.getAllSounds()

    val allVideos: LiveData<List<Video>> = videoDao.getAllVideos()

    val allDayEntitiesWithRelatedSortedByDate: LiveData<List<DayWithTodosAndEvents>> =
        dayDao.getAllDayEntitiesWithRelatedSortedByDate()

    fun getSoundById(id: Long): Sound? {
        return soundDao.getSoundById(id)
    }

    suspend fun updateSound(sound: Sound) {
        soundDao.updateSound(sound)
    }

    suspend fun addNewSound(sound: Sound) {
        soundDao.addNewSound(sound)
    }

    fun getVideoById(id: Long): Video? {
        return videoDao.getVideoById(id)
    }

    suspend fun updateVideo(video: Video) {
        videoDao.updateVideo(video)
    }

    suspend fun addNewVideo(video: Video) {
        videoDao.addNewVideo(video)
    }

    fun getImageById(id: Long): Image? {
        return imageDao.getImageById(id)
    }

    suspend fun updateImage(image: Image) {
        imageDao.updateImage(image)
    }

    suspend fun addNewImage(image: Image) {
        imageDao.addNewImage(image)
    }

    suspend fun deleteNoteEntity(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteSoundEntity(sound: Sound) {
        soundDao.deleteSound(sound)
    }

    suspend fun deleteVideoEntity(video: Video) {
        videoDao.deleteVideo(video)
    }

    suspend fun deleteImageEntity(image: Image) {
        imageDao.deleteImage(image)
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

    suspend fun addNewNote(note: Note) {
        noteDao.addNewNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }

    fun getNoteByDate(date: LocalDate): Note? {
        return noteDao.getNoteByDate(date)
    }

    fun getDayByDate(date: LocalDate): DayEntity? {
        return dayDao.getDayByDate(date)
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