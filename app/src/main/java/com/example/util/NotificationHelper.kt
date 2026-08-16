package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.CycleStatus
import com.example.model.UserSettingsEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

object NotificationHelper {

    const val CHANNEL_PERIOD_ID = "period_reminders_channel"
    const val CHANNEL_DAILY_ID = "daily_reminders_channel"
    const val CHANNEL_FERTILE_ID = "fertile_reminders_channel"
    const val CHANNEL_HYDRATION_ID = "hydration_reminders_channel"

    const val NOTIF_ID_PERIOD = 1001
    const val NOTIF_ID_DAILY = 1002
    const val NOTIF_ID_FERTILE = 1003
    const val NOTIF_ID_HYDRATION = 1004
    const val NOTIF_ID_TEST = 1005

    const val REQUEST_CODE_DAILY = 2001
    const val REQUEST_CODE_PERIOD = 2002
    const val REQUEST_CODE_FERTILE = 2003

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val periodChannel = NotificationChannel(
                CHANNEL_PERIOD_ID,
                "Cycle & Period Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle notifications for upcoming periods and cycle changes"
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_ID,
                "Daily Wellness Check-in",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to log symptoms, moods, and self-care"
            }

            val fertileChannel = NotificationChannel(
                CHANNEL_FERTILE_ID,
                "Fertility & Ovulation Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when your fertile window approaches"
            }

            val hydrationChannel = NotificationChannel(
                CHANNEL_HYDRATION_ID,
                "Hydration & Self-Care",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Gentle reminders to drink water and take care of your body"
            }

            notificationManager.createNotificationChannel(periodChannel)
            notificationManager.createNotificationChannel(dailyChannel)
            notificationManager.createNotificationChannel(fertileChannel)
            notificationManager.createNotificationChannel(hydrationChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        message: String,
        isPrivate: Boolean = false
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val displayTitle = if (isPrivate) {
            when (channelId) {
                CHANNEL_PERIOD_ID -> "Self-Care Reminder 🌸"
                CHANNEL_FERTILE_ID -> "Cycle Reminder 💜"
                else -> "Daily Check-in 🌸"
            }
        } else {
            title
        }

        val displayMessage = if (isPrivate) {
            "Time for your scheduled wellness moment 💖"
        } else {
            message
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(displayTitle)
            .setContentText(displayMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(displayMessage))
            .setPriority(
                if (channelId == CHANNEL_PERIOD_ID) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun sendTestNotification(context: Context, isAmharic: Boolean = false, isPrivate: Boolean = false) {
        val title = if (isPrivate) {
            if (isAmharic) "የራስ እንክብካቤ ማስታወሻ 🌸" else "Gentle Reminder 🌸"
        } else {
            if (isAmharic) "የማስታወሻ ሙከራ 🌸" else "Gentle Reminder Test 🌸"
        }

        val message = if (isPrivate) {
            if (isAmharic) "ማሳወቂያዎች በትክክል እየሰሩ ነው 💖" else "Your notifications are active and discreetly configured 💖"
        } else {
            if (isAmharic)
                "የወር አበባና የጤና ማስታወሻዎች በትክክል እየሰሩ ነው! ራስን መንከባከብ አይርሱ 💖"
            else
                "Your gentle cycle and wellness reminders are active and ready! Take care of yourself today 💖"
        }

        showNotification(
            context = context,
            channelId = CHANNEL_DAILY_ID,
            notificationId = NOTIF_ID_TEST,
            title = title,
            message = message,
            isPrivate = isPrivate
        )
    }

    fun scheduleDailyReminder(context: Context, timeStr: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.ACTION_DAILY_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val parts = timeStr.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
        }
    }

    fun schedulePeriodApproachingReminder(
        context: Context,
        predictedPeriodStart: LocalDate?,
        daysBefore: Int
    ) {
        if (predictedPeriodStart == null) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val reminderDate = predictedPeriodStart.minusDays(daysBefore.toLong())
        val today = LocalDate.now()
        if (reminderDate.isBefore(today)) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.ACTION_PERIOD_REMINDER"
            putExtra("daysBefore", daysBefore)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PERIOD,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val triggerTime = LocalDateTime.of(reminderDate, LocalTime.of(9, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (_: Exception) {
        }
    }

    fun rescheduleAllReminders(
        context: Context,
        settings: UserSettingsEntity?,
        cycleStatus: CycleStatus?
    ) {
        val userSettings = settings ?: UserSettingsEntity()

        if (userSettings.isDailyCheckInReminderEnabled) {
            scheduleDailyReminder(context, userSettings.dailyReminderTime)
        } else {
            cancelDailyReminder(context)
        }

        if (userSettings.isPeriodReminderEnabled && cycleStatus?.expectedNextPeriodDate != null) {
            schedulePeriodApproachingReminder(
                context,
                cycleStatus.expectedNextPeriodDate,
                userSettings.periodReminderDaysBefore
            )
        } else {
            cancelPeriodReminder(context)
        }
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val dailyIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.ACTION_DAILY_REMINDER"
        }
        val dailyPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY,
            dailyIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (dailyPending != null) {
            alarmManager.cancel(dailyPending)
        }
    }

    fun cancelPeriodReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val periodIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.ACTION_PERIOD_REMINDER"
        }
        val periodPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PERIOD,
            periodIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (periodPending != null) {
            alarmManager.cancel(periodPending)
        }
    }

    fun cancelAllReminders(context: Context) {
        cancelDailyReminder(context)
        cancelPeriodReminder(context)
    }
}
