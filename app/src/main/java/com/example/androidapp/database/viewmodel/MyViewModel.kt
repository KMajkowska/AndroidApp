package com.example.androidapp.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidapp.database.MyDatabaseConnection
import com.example.androidapp.database.model.ConnectedToDay
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.Note
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.database.model.savables.Image
import com.example.androidapp.database.model.savables.Saveable
import com.example.androidapp.database.model.savables.Sound
import com.example.androidapp.database.model.savables.Video
import com.example.androidapp.database.repository.MyRepository
import com.example.androidapp.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayViewModel(
    application: Application,
    private val notificationHelper: NotificationHelper,
    databaseConnection: MyDatabaseConnection
) : AndroidViewModel(application) {

    private val repository: MyRepository = MyRepository(
        databaseConnection.dayDao(),
        databaseConnection.eventDao(),
        databaseConnection.noteDao(),
        databaseConnection.todoDao(),
        databaseConnection.imageDao(),
        databaseConnection.soundDao(),
        databaseConnection.videoDao()
    )

    val allDayEntitiesSortedByDate: LiveData<List<DayEntity>> =
        repository.allDayEntitiesSortedByDate
    val allDayEntitiesWithRelatedSortedByDate: LiveData<List<DayWithTodosAndEvents>> =
        repository.allDayEntitiesWithRelatedSortedByDate
    val allTodoEntities: LiveData<List<TodoEntity>> = repository.allTodoEntities
    val allEventEntities: LiveData<List<EventEntity>> = repository.allEventEntities

    val allImages: LiveData<List<Image>> = repository.allImages
    val allSounds: LiveData<List<Sound>> = repository.allSounds
    val allVideos: LiveData<List<Video>> = repository.allVideos
    val allNotes: LiveData<List<Note>> = repository.allNotes

    val allConnectedToDay: LiveData<List<ConnectedToDay>> = MediatorLiveData<List<ConnectedToDay>>().apply {
        val combinedList = arrayListOf<ConnectedToDay>()

        fun <T : ConnectedToDay> addSource(source: LiveData<List<T>>) {
            addSource(source) { items ->
                combinedList.removeAll { it.javaClass == items.firstOrNull()?.javaClass }
                combinedList.addAll(items)
                combinedList.sortBy { it.id }
                value = combinedList
            }
        }

        addSource(repository.allImages)
        addSource(repository.allSounds)
        addSource(repository.allVideos)
        addSource(repository.allNotes)
    }

    fun getSoundById(id: Long): Sound? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getSoundById(id)
            }
        }
    }

    fun getVideoById(id: Long): Video? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getVideoById(id)
            }
        }
    }

    fun getImageById(id: Long): Image? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getImageById(id)
            }
        }
    }

    fun addNewVideo(video: Video) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewVideo(video)
        }
    }

    fun addNewSound(sound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewSound(sound)
        }
    }

    fun addNewImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewImage(image)
        }
    }

    fun deleteConnectedToDay(connectedToDay: ConnectedToDay) {
        viewModelScope.launch(Dispatchers.IO) {
            if (connectedToDay is Saveable)
                connectedToDay.doBeforeDeletingRecord()

            when (connectedToDay) {
                is Image -> repository.deleteImageEntity(connectedToDay)
                is Note -> repository.deleteNoteEntity(connectedToDay)
                is Sound -> repository.deleteSoundEntity(connectedToDay)
                is Video -> repository.deleteVideoEntity(connectedToDay)
            }
        }
    }

    fun updateVideo(video: Video) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateVideo(video)
        }
    }

    fun updateSound(sound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSound(sound)
        }
    }

    fun updateImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateImage(image)
        }
    }

    private fun scheduleNotification(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationHelper.scheduleNotification(repository.getDayIdWithRelatedByDate(date))
        }
    }

    fun saveDayEntity(dayEntity: DayEntity) {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.saveDayEntity(dayEntity)
                scheduleNotification(dayEntity.date)
            }
        }
    }

    fun deleteDayEntity(dayEntity: DayEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDayEntity(dayEntity)
            notificationHelper.unscheduleNotification(dayEntity.dayId)
        }
    }

    private fun saveAndRetrieveDayEntity(date: LocalDate): DayEntity {
        val dayEntity = DayEntity(date = date)
        saveDayEntity(dayEntity)
        return getInnerDayByDate(date)!!
    }

    fun getDayByDate(chosenDate: LocalDate): DayEntity {
        return getInnerDayByDate(chosenDate) ?: saveAndRetrieveDayEntity(chosenDate)
    }

    fun getNoteByDate(chosenDate: LocalDate): Note? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getNoteByDate(chosenDate)
            }
        }
    }

    fun saveTodoEntity(todoEntity: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTodoEntity(todoEntity)
        }
    }

    fun deleteTodoEntity(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTodoEntity(todo)
        }
    }

    fun getInnerDayByDate(date: LocalDate): DayEntity? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayByDate(date)
            }
        }
    }

    fun saveEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveEventEntity(eventEntity)
        }
    }

    fun deleteEventEntity(eventEntity: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventEntity(eventEntity)
        }
    }

    fun addNewNote(note: Note) {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.addNewNote(note)
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun getNoteById(id: Long): Note? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getNoteById(id)
            }
        }
    }

    fun getEventsByDayId(dayId: Long): LiveData<List<EventEntity>> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getEventsByDayId(dayId)
            }
        }
    }

    fun getTodosByDayId(dayId: Long): LiveData<List<TodoEntity>> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getTodosByDayId(dayId)
            }
        }
    }

    private fun getDayIdWithRelated(dayId: Long): DayWithTodosAndEvents? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getDayIdWithRelated(dayId)
            }
        }
    }

    fun deleteDayEntityIfEmpty(dayId: Long) {
        val temp: DayWithTodosAndEvents? = getDayIdWithRelated(dayId)
        if (temp?.events?.isEmpty() == true &&
            temp.todos.isEmpty() &&
            temp.dayEntity.dayTitle.trim().isEmpty()
        )
            deleteDayEntity(temp.dayEntity)
    }
}

class DayViewModelFactory(
    private val application: Application,
    private val notificationHelper: NotificationHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DayViewModel(
                application,
                notificationHelper,
                MyDatabaseConnection.getDatabase(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
