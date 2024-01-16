package com.example.androidapp

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidapp.database.model.DayEntity
import com.example.androidapp.database.model.DayWithTodosAndEvents
import com.example.androidapp.database.model.EventEntity
import com.example.androidapp.database.model.TodoEntity
import com.example.androidapp.notifications.NotificationHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class NotificationHelperTest {

    private lateinit var mockContext: Context
    private lateinit var mockAlarmManager: AlarmManager
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var mockNotificationManager: NotificationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockContext = mockk(relaxed = true)
        mockAlarmManager = mockk(relaxed = true)
        mockNotificationManager = mockk(relaxed = true)

        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        every { mockContext.getSystemService(Context.ALARM_SERVICE) } returns mockAlarmManager

        every { mockNotificationManager.notify(any(), any()) } just Runs
        every { mockNotificationManager.cancel(any()) } just Runs

        notificationHelper = NotificationHelper(mockContext, true)
    }

    @Ignore("This will not work on the emulator, as it doesn't allow notifications!")
    @Test
    fun testSchedulingWithFutureDate() {
        val dayWithTodosAndEvents = createDayWithTodosAndEventsFutureDate()

        notificationHelper.scheduleNotification(dayWithTodosAndEvents)

        verify(exactly = 1) { mockAlarmManager.setExact(any(), any(), any()) }
    }

    @Ignore("This will not work on the emulator, as it doesn't allow notifications!")
    @Test
    fun testShouldNotDuplicateForSameDayEntity() {
        val dayWithTodosAndEvents = createDayWithTodosAndEventsFutureDate()

        notificationHelper.scheduleNotification(dayWithTodosAndEvents)
        notificationHelper.scheduleNotification(dayWithTodosAndEvents)

        verify(exactly = 1) { mockAlarmManager.setExact(any(), any(), any()) }
    }

    @Test
    fun testScheduleWithNullDayWithRelated() {
        // Act
        notificationHelper.scheduleNotification(null)

        // Assert
        verify(exactly = 0) { mockAlarmManager.setExact(any(), any(), any()) }
    }

    @Test
    fun testCancelForNullObject() {
        // Act
        notificationHelper.unscheduleNotification(null)

        // Assert
        verify(exactly = 0) { mockAlarmManager.cancel(any<PendingIntent>()) }
    }


    @Test
    fun testSchedulingWithPreviousDate() {
        val dayWithTodosAndEvents = createDayWithTodosAndEventsPastDate()

        notificationHelper.scheduleNotification(dayWithTodosAndEvents)

        verify(exactly = 0) { mockAlarmManager.setExact(any(), any(), any()) }
    }

    // Additional test cases...

    private fun createDayWithTodosAndEventsFutureDate(): DayWithTodosAndEvents {
        return createDayWithRelated(LocalDate.now().plusDays(1), 1, 1)
    }

    private fun createDayWithTodosAndEventsPastDate(): DayWithTodosAndEvents {
        return createDayWithRelated(LocalDate.now().minusDays(1), 1, 1)
    }

    private fun createDayWithRelated(
        localDate: LocalDate,
        amountOfEvents: Int,
        amountOfTodos: Int
    ): DayWithTodosAndEvents {
        val dayEntity = DayEntity(dayId = 1, dayTitle = "Some title", date = localDate)

        val events = createListOfEvents(amountOfEvents, dayEntity.dayId!!)
        val todos = createListOfTodos(amountOfTodos, dayEntity.dayId!!)

        return DayWithTodosAndEvents(dayEntity, todos, events)
    }

    private fun createListOfEvents(n: Int, dayId: Long): List<EventEntity> {
        val list = mutableListOf<EventEntity>()
        for (i in 1..n)
            list.add(EventEntity(eventId = i.toLong(), dayForeignId = dayId))

        return list
    }

    private fun createListOfTodos(n: Int, dayId: Long): List<TodoEntity> {
        val list = mutableListOf<TodoEntity>()
        for (i in 1..n)
            list.add(TodoEntity(todoId = i.toLong(), dayForeignId = dayId))

        return list
    }
}
