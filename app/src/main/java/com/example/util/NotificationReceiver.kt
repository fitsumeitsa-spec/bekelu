package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import com.example.util.CycleEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val settings = db.userSettingsDao().getUserSettingsDirect()
                val isAmharic = settings?.language == "am"
                val isPrivate = settings?.isPrivateNotifications == true

                when (action) {
                    Intent.ACTION_BOOT_COMPLETED -> {
                        // Re-schedule all reminders using latest cycle data
                        val periods = db.periodDao().getAllPeriodsDirect()
                        val cycleStatus = CycleEngine.calculateCycleStatus(
                            periods,
                            settings?.avgCycleLength ?: 28,
                            settings?.avgPeriodLength ?: 5
                        )
                        NotificationHelper.rescheduleAllReminders(context, settings, cycleStatus)
                    }

                    "com.example.ACTION_PERIOD_REMINDER" -> {
                        if (settings?.isPeriodReminderEnabled != false) {
                            val daysBefore = intent.getIntExtra("daysBefore", settings?.periodReminderDaysBefore ?: 2)
                            val title = if (isAmharic) "የወር አበባ ዑደት ማሳሰቢያ 🌸" else "Upcoming Period Reminder 🌸"
                            val message = if (isAmharic)
                                "የወር አበባ ዑደት ከ $daysBefore ቀናት በኋላ እንደሚጀምር ይገመታል። መልካም ራስ-እንክብካቤ ጊዜ ይሁንልዎ! 🌸"
                            else
                                "Your period is predicted to begin in $daysBefore days. Take gentle care today 🌸"

                            NotificationHelper.showNotification(
                                context = context,
                                channelId = NotificationHelper.CHANNEL_PERIOD_ID,
                                notificationId = NotificationHelper.NOTIF_ID_PERIOD,
                                title = title,
                                message = message,
                                isPrivate = isPrivate
                            )
                        }
                    }

                    "com.example.ACTION_DAILY_REMINDER" -> {
                        if (settings?.isDailyCheckInReminderEnabled != false) {
                            val title = if (isAmharic) "የዕለት ተዕለት የጤና ማስታወሻ 🌸" else "Daily Wellness Check-in 🌸"
                            val message = if (isAmharic)
                                "ዛሬ ጤናና ስሜት እንዴት ነው? ምልክቶችንና ስሜትን ለመመዝገብ አጭር ጊዜ ይውሰዱ 💖"
                            else
                                "How are you feeling today? Take a peaceful moment to log your wellness and symptoms 💖"

                            NotificationHelper.showNotification(
                                context = context,
                                channelId = NotificationHelper.CHANNEL_DAILY_ID,
                                notificationId = NotificationHelper.NOTIF_ID_DAILY,
                                title = title,
                                message = message,
                                isPrivate = isPrivate
                            )
                        }
                    }

                    "com.example.ACTION_FERTILE_REMINDER" -> {
                        if (settings?.isFertileReminderEnabled != false) {
                            val title = if (isAmharic) "የመራባት ቀናት ማሳሰቢያ 💜" else "Fertile Window Alert 💜"
                            val message = if (isAmharic)
                                "የመራባት ቀናት ዛሬ ይጀምራሉ ተብሎ ይገመታል። የቀን መቁጠሪያውን ይመልከቱ 🌸"
                            else
                                "Your fertile window is estimated to start today. Check your cycle calendar for details 🌸"

                            NotificationHelper.showNotification(
                                context = context,
                                channelId = NotificationHelper.CHANNEL_FERTILE_ID,
                                notificationId = NotificationHelper.NOTIF_ID_FERTILE,
                                title = title,
                                message = message,
                                isPrivate = isPrivate
                            )
                        }
                    }

                    "com.example.ACTION_TEST_REMINDER" -> {
                        NotificationHelper.sendTestNotification(context, isAmharic, isPrivate)
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
