package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "periods")
data class PeriodRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: String, // yyyy-MM-dd
    val endDate: String? = null, // yyyy-MM-dd or null if ongoing
    val flowIntensity: String = "Medium",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_logs")
data class DailyLogRecord(
    @PrimaryKey
    val date: String, // yyyy-MM-dd
    val mood: String? = null, // Happy, Calm, Loved, Neutral, Sad, Stressed, Tired
    val flow: String? = null, // None, Light, Medium, Heavy
    val symptoms: List<String> = emptyList(), // Cramps, Headache, Bloating, Fatigue, Acne, etc.
    val energy: String? = null, // Low, Normal, High
    val hadSex: Boolean = false, // Sexual intercourse on this date
    val sexProtection: String? = null, // Protected, Unprotected, High Desire, Solo
    val sexOrgasm: Boolean = false,
    val notes: String = "",
    val waterGlasses: Int = 4,
    val sleepHours: Float = 7.5f,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Selam",
    val language: String = "en", // "en" or "am"
    val calendarSystem: String = "ETHIOPIAN", // "GREGORIAN" or "ETHIOPIAN"
    val avgCycleLength: Int = 28,
    val avgPeriodLength: Int = 5,
    val isAppLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val isPrivateNotifications: Boolean = false,
    val isPeriodReminderEnabled: Boolean = true,
    val periodReminderDaysBefore: Int = 2,
    val isDailyCheckInReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "20:00",
    val isFertileReminderEnabled: Boolean = true,
    val isHydrationReminderEnabled: Boolean = false,
    val isOnboardingCompleted: Boolean = false
)

enum class CyclePhase(val rawName: String) {
    MENSTRUAL("Menstrual"),
    FOLLICULAR("Follicular"),
    OVULATION("Ovulation"),
    LUTEAL("Luteal")
}

data class CycleStatus(
    val currentCycleDay: Int,
    val totalCycleLength: Int,
    val periodLength: Int,
    val phase: CyclePhase,
    val daysUntilNextPeriod: Int,
    val expectedNextPeriodDate: LocalDate,
    val isPeriodActiveToday: Boolean,
    val isOvulationDayToday: Boolean,
    val isFertileWindowToday: Boolean,
    val regularity: String,
    val lastPeriodStartDate: LocalDate?
)

data class MoodItem(
    val key: String,
    val emoji: String,
    val labelEn: String,
    val labelAm: String
)

data class SymptomItem(
    val key: String,
    val iconEmoji: String,
    val labelEn: String,
    val labelAm: String
)

data class FlowItem(
    val key: String,
    val iconEmoji: String,
    val labelEn: String,
    val labelAm: String,
    val levelNumber: Int
)

data class EnergyItem(
    val key: String,
    val iconEmoji: String,
    val labelEn: String,
    val labelAm: String
)

data class IntimacyItem(
    val key: String,
    val iconEmoji: String,
    val labelEn: String,
    val labelAm: String
)

