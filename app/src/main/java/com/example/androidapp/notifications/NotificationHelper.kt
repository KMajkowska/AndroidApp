package com.example.androidapp.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.androidapp.R
import com.example.androidapp.database.model.DayWithTodosAndEvents
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Objects

class NotificationHelper(
    private val context: Context,
    private val areNotificationsEnabled: Boolean
) {

    companion object {
        private const val CHANNEL_ID = "calendar_event_channel"
        private const val NOTIFICATION_ID_STRING = "notificationId"
        private const val NOTIFICATION_DATA_STRING = "notificationData"
        private const val NOTIFICATION_EVENT_AMOUNT = "notificationEventsAmount"
        private const val NOTIFICATION_TODO_AMOUNT = "notificationTodoAmount"
        private const val ARE_NOTIFICATIONS_ENABLED_STRING = "areNotificationsEnabled"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Event Reminder Channel"
        val descriptionText = "Channel for Event Reminder Notifications"
        val channel =
            NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    fun scheduleNotification(dayWithTodosAndEvents: DayWithTodosAndEvents?) {
        if (Objects.isNull(dayWithTodosAndEvents) ||
            Objects.isNull(dayWithTodosAndEvents!!.dayEntity) ||
            Objects.isNull(dayWithTodosAndEvents.dayEntity.dayId)
        )
            return

        val today = LocalDateTime.now()
        if (!dayWithTodosAndEvents.dayEntity.date.isAfter(today.toLocalDate()))
            return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(
                ARE_NOTIFICATIONS_ENABLED_STRING,
                areNotificationsEnabled
            )
            putExtra(
                NOTIFICATION_ID_STRING,
                dayWithTodosAndEvents.dayEntity.dayId
            )
            putExtra(
                NOTIFICATION_EVENT_AMOUNT,
                if (Objects.isNull(dayWithTodosAndEvents.events)) 0 else dayWithTodosAndEvents.events.size
            )
            putExtra(
                NOTIFICATION_TODO_AMOUNT,
                if (Objects.isNull(dayWithTodosAndEvents.todos)) 0 else dayWithTodosAndEvents.todos.size
            )
            putExtra(
                NOTIFICATION_DATA_STRING,
                dayWithTodosAndEvents.dayEntity.dayTitle
            )
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dayWithTodosAndEvents.dayEntity.dayId!!.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE
        )

        if (pendingIntent == null) {
            val newPendingIntent = PendingIntent.getBroadcast(
                context,
                dayWithTodosAndEvents.dayEntity.dayId!!.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                ZonedDateTime.of(
                    dayWithTodosAndEvents.dayEntity.date.atStartOfDay(),
                    ZoneId.systemDefault()
                ).toInstant().toEpochMilli(),
                newPendingIntent
            )
        }
    }

    fun unscheduleNotification(objectId: Long?) {
        if (Objects.isNull(objectId))
            return

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            objectId!!.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }


    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.areNotificationsEnabled() && intent.getBooleanExtra(
                    ARE_NOTIFICATIONS_ENABLED_STRING,
                    true
                )
            ) {
                val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.resources.getString(R.string.notification_title))
                    .setContentText(
                        "${context.resources.getString(R.string.title)}: ${
                            intent.getStringExtra(
                                NOTIFICATION_DATA_STRING
                            )
                        } ${context.resources.getString(R.string.events)}: ${
                            intent.getIntExtra(
                                NOTIFICATION_EVENT_AMOUNT, 0
                            )
                        } ${context.resources.getString(R.string.todos)}: ${
                            intent.getIntExtra(
                                NOTIFICATION_TODO_AMOUNT, 0
                            )
                        }"
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                notificationManager.notify(
                    intent.getIntExtra(NOTIFICATION_ID_STRING, 0),
                    notificationBuilder.build()
                )
            }
        }
    }
}
