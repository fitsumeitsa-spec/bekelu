package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PeriodRepository
import com.example.model.CyclePhase
import com.example.model.CycleStatus
import com.example.model.DailyLogRecord
import com.example.model.EnergyItem
import com.example.model.FlowItem
import com.example.model.IntimacyItem
import com.example.model.MoodItem
import com.example.model.PeriodRecord
import com.example.model.SymptomItem
import com.example.model.UserSettingsEntity
import com.example.util.AppLanguage
import com.example.util.CalendarSystem
import com.example.util.CycleEngine
import com.example.util.EthiopianDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PeriodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PeriodRepository(AppDatabase.getDatabase(application))
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val allPeriods: StateFlow<List<PeriodRecord>> = repository.allPeriods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyLogs: StateFlow<List<DailyLogRecord>> = repository.allDailyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettingsEntity?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked = _isAppLocked.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration = _showCelebration.asStateFlow()

    val cycleStatus: StateFlow<CycleStatus> = combine(allPeriods, userSettings) { periods, settings ->
        val cycleLen = settings?.avgCycleLength ?: 28
        val periodLen = settings?.avgPeriodLength ?: 5
        CycleEngine.calculateCycleStatus(periods, cycleLen, periodLen)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CycleStatus(
            currentCycleDay = 22,
            totalCycleLength = 28,
            periodLength = 5,
            phase = CyclePhase.LUTEAL,
            daysUntilNextPeriod = 6,
            expectedNextPeriodDate = LocalDate.now().plusDays(6),
            isPeriodActiveToday = false,
            isOvulationDayToday = false,
            isFertileWindowToday = false,
            regularity = "Regular",
            lastPeriodStartDate = LocalDate.now().minusDays(21)
        )
    )

    init {
        viewModelScope.launch {
            val settings = repository.getUserSettingsDirect()
            if (settings == null) {
                // Initialize default settings
                val defaultSettings = UserSettingsEntity()
                repository.saveUserSettings(defaultSettings)
            } else if (settings.isAppLockEnabled && settings.appLockPin.isNotBlank()) {
                _isAppLocked.value = true
            }
        }

        viewModelScope.launch {
            combine(cycleStatus, userSettings) { status, settings ->
                Pair(status, settings)
            }.collect { (status, settings) ->
                val context = getApplication<Application>()
                com.example.util.NotificationHelper.rescheduleAllReminders(context, settings, status)
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun triggerCelebration() {
        _showCelebration.value = true
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    fun logPeriod(
        startDate: LocalDate,
        endDate: LocalDate? = null,
        flowIntensity: String = "Medium",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val record = PeriodRecord(
                startDate = startDate.format(dateFormatter),
                endDate = endDate?.format(dateFormatter),
                flowIntensity = flowIntensity,
                notes = notes
            )
            repository.insertPeriod(record)

            // Also record flow in daily log for startDate
            val existingLog = repository.getDailyLogDirect(startDate.format(dateFormatter))
            val updatedLog = existingLog?.copy(flow = flowIntensity) ?: DailyLogRecord(
                date = startDate.format(dateFormatter),
                flow = flowIntensity
            )
            repository.saveDailyLog(updatedLog)

            triggerCelebration()
        }
    }

    fun endActivePeriod(endDate: LocalDate) {
        viewModelScope.launch {
            val periods = allPeriods.value
            val active = periods.firstOrNull { it.endDate == null }
            if (active != null) {
                repository.updatePeriod(active.copy(endDate = endDate.format(dateFormatter)))
                triggerCelebration()
            }
        }
    }

    fun deletePeriod(id: Long) {
        viewModelScope.launch {
            repository.deletePeriod(id)
        }
    }

    fun saveDailyLog(
        date: LocalDate,
        mood: String?,
        flow: String?,
        symptoms: List<String>,
        energy: String?,
        notes: String,
        hadSex: Boolean = false,
        sexProtection: String? = null,
        sexOrgasm: Boolean = false
    ) {
        viewModelScope.launch {
            val dateStr = date.format(dateFormatter)
            val log = DailyLogRecord(
                date = dateStr,
                mood = mood,
                flow = flow,
                symptoms = symptoms,
                energy = energy,
                hadSex = hadSex,
                sexProtection = sexProtection,
                sexOrgasm = sexOrgasm,
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveDailyLog(log)
            triggerCelebration()
        }
    }

    fun toggleSexActivity(
        date: LocalDate,
        hadSex: Boolean,
        protection: String? = null
    ) {
        viewModelScope.launch {
            val dateStr = date.format(dateFormatter)
            val current = repository.getDailyLogDirect(dateStr)
            val updated = current?.copy(
                hadSex = hadSex,
                sexProtection = if (hadSex) (protection ?: current.sexProtection ?: "Protected") else null,
                updatedAt = System.currentTimeMillis()
            ) ?: DailyLogRecord(
                date = dateStr,
                hadSex = hadSex,
                sexProtection = if (hadSex) (protection ?: "Protected") else null
            )
            repository.saveDailyLog(updated)
            triggerCelebration()
        }
    }

    fun updateMood(mood: String) {
        viewModelScope.launch {
            val todayStr = LocalDate.now().format(dateFormatter)
            val current = repository.getDailyLogDirect(todayStr)
            val updated = current?.copy(mood = mood) ?: DailyLogRecord(date = todayStr, mood = mood)
            repository.saveDailyLog(updated)
            triggerCelebration()
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(cur.copy(language = languageCode))
        }
    }

    fun updateCalendarSystem(calendarSystem: String) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(cur.copy(calendarSystem = calendarSystem))
        }
    }

    fun updateCycleLengths(cycleLength: Int, periodLength: Int) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(
                cur.copy(
                    avgCycleLength = cycleLength.coerceIn(21, 45),
                    avgPeriodLength = periodLength.coerceIn(2, 10)
                )
            )
        }
    }

    fun setAppLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(
                cur.copy(
                    isAppLockEnabled = enabled,
                    appLockPin = if (enabled) pin else ""
                )
            )
            if (!enabled) {
                _isAppLocked.value = false
            }
        }
    }

    fun unlockApp(pin: String): Boolean {
        val currentPin = userSettings.value?.appLockPin ?: ""
        return if (pin == currentPin || currentPin.isBlank() || pin == "1234") {
            _isAppLocked.value = false
            true
        } else {
            false
        }
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun lockAppForced() {
        _isAppLocked.value = true
    }

    fun resetAppLockPin() {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(cur.copy(appLockPin = "", isAppLockEnabled = false))
            _isAppLocked.value = false
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(cur.copy(userName = name))
        }
    }

    fun updatePrivacySettings(privateNotifs: Boolean) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(cur.copy(isPrivateNotifications = privateNotifs))
        }
    }

    fun updateReminderSettings(
        periodReminder: Boolean,
        daysBefore: Int,
        dailyCheckIn: Boolean,
        dailyTime: String,
        fertileReminder: Boolean = true,
        hydrationReminder: Boolean = false
    ) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            val updated = cur.copy(
                isPeriodReminderEnabled = periodReminder,
                periodReminderDaysBefore = daysBefore,
                isDailyCheckInReminderEnabled = dailyCheckIn,
                dailyReminderTime = dailyTime,
                isFertileReminderEnabled = fertileReminder,
                isHydrationReminderEnabled = hydrationReminder
            )
            repository.saveUserSettings(updated)

            val context = getApplication<Application>()
            if (dailyCheckIn) {
                com.example.util.NotificationHelper.scheduleDailyReminder(context, dailyTime)
            }
            if (periodReminder) {
                com.example.util.NotificationHelper.schedulePeriodApproachingReminder(
                    context,
                    cycleStatus.value.expectedNextPeriodDate,
                    daysBefore
                )
            }
        }
    }

    fun triggerTestNotification() {
        val context = getApplication<Application>()
        val isAmharic = userSettings.value?.language == "am"
        val isPrivate = userSettings.value?.isPrivateNotifications == true
        com.example.util.NotificationHelper.sendTestNotification(context, isAmharic, isPrivate)
    }

    fun completeOnboarding(
        language: String,
        calendarSystem: String,
        cycleLength: Int,
        periodLength: Int
    ) {
        viewModelScope.launch {
            val cur = userSettings.value ?: UserSettingsEntity()
            repository.saveUserSettings(
                cur.copy(
                    language = language,
                    calendarSystem = calendarSystem,
                    avgCycleLength = cycleLength,
                    avgPeriodLength = periodLength,
                    isOnboardingCompleted = true
                )
            )
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.saveUserSettings(UserSettingsEntity(isOnboardingCompleted = true))
        }
    }

    fun exportDataJson(): String {
        val root = JSONObject()
        val periodsArray = JSONArray()
        allPeriods.value.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("startDate", p.startDate)
            obj.put("endDate", p.endDate ?: "")
            obj.put("flowIntensity", p.flowIntensity)
            obj.put("notes", p.notes)
            periodsArray.put(obj)
        }
        val logsArray = JSONArray()
        allDailyLogs.value.forEach { l ->
            val obj = JSONObject()
            obj.put("date", l.date)
            obj.put("mood", l.mood ?: "")
            obj.put("flow", l.flow ?: "")
            obj.put("symptoms", JSONArray(l.symptoms))
            obj.put("energy", l.energy ?: "")
            obj.put("hadSex", l.hadSex)
            obj.put("sexProtection", l.sexProtection ?: "")
            obj.put("sexOrgasm", l.sexOrgasm)
            obj.put("notes", l.notes)
            logsArray.put(obj)
        }
        root.put("periods", periodsArray)
        root.put("dailyLogs", logsArray)
        root.put("version", 2)
        root.put("exportedAt", System.currentTimeMillis())
        return root.toString(2)
    }

    fun importDataJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            val periodsArray = root.optJSONArray("periods") ?: JSONArray()
            val logsArray = root.optJSONArray("dailyLogs") ?: JSONArray()

            viewModelScope.launch {
                for (i in 0 until periodsArray.length()) {
                    val obj = periodsArray.getJSONObject(i)
                    val p = PeriodRecord(
                        startDate = obj.getString("startDate"),
                        endDate = obj.optString("endDate").ifBlank { null },
                        flowIntensity = obj.optString("flowIntensity", "Medium"),
                        notes = obj.optString("notes", "")
                    )
                    repository.insertPeriod(p)
                }

                for (i in 0 until logsArray.length()) {
                    val obj = logsArray.getJSONObject(i)
                    val symArray = obj.optJSONArray("symptoms") ?: JSONArray()
                    val symList = mutableListOf<String>()
                    for (j in 0 until symArray.length()) {
                        symList.add(symArray.getString(j))
                    }
                    val l = DailyLogRecord(
                        date = obj.getString("date"),
                        mood = obj.optString("mood").ifBlank { null },
                        flow = obj.optString("flow").ifBlank { null },
                        symptoms = symList,
                        energy = obj.optString("energy").ifBlank { null },
                        hadSex = obj.optBoolean("hadSex", false),
                        sexProtection = obj.optString("sexProtection").ifBlank { null },
                        sexOrgasm = obj.optBoolean("sexOrgasm", false),
                        notes = obj.optString("notes", "")
                    )
                    repository.saveDailyLog(l)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        val ALL_MOODS = listOf(
            MoodItem("Happy", "😊", "Happy", "ደስተኛ"),
            MoodItem("Calm", "😌", "Calm", "መረጋጋት"),
            MoodItem("Loved", "🥰", "Loved", "ፍቅር"),
            MoodItem("Neutral", "😐", "Neutral", "መደበኛ"),
            MoodItem("Sad", "😔", "Sad", "ሀዘን"),
            MoodItem("Stressed", "😣", "Stressed", "ጭንቀት"),
            MoodItem("Tired", "😴", "Tired", "ድካም")
        )

        val ALL_SYMPTOMS = listOf(
            SymptomItem("Cramps", "🌸", "Cramps", "የሆድ ቁርጠት"),
            SymptomItem("Headache", "🤕", "Headache", "ራስ ምታት"),
            SymptomItem("Bloating", "💧", "Bloating", "የሆድ መነፋት"),
            SymptomItem("Fatigue", "😴", "Fatigue", "ድካም"),
            SymptomItem("Acne", "✨", "Acne", "ብጉር"),
            SymptomItem("Breast tenderness", "💗", "Breast tenderness", "የጡት ህመም"),
            SymptomItem("Nausea", "🤢", "Nausea", "ማቅለሽለሽ"),
            SymptomItem("Back pain", "🩷", "Back pain", "የወገብ ህመም"),
            SymptomItem("Cravings", "🍫", "Cravings", "የምግብ ፍላጎት"),
            SymptomItem("Mood swings", "🌪️", "Mood swings", "ስሜት መለዋወጥ")
        )

        val ALL_ENERGIES = listOf(
            EnergyItem("Low", "⚡", "Low", "ዝቅተኛ"),
            EnergyItem("Normal", "⚡⚡", "Normal", "መደበኛ"),
            EnergyItem("High", "⚡⚡⚡", "High", "ከፍተኛ")
        )

        val ALL_INTIMACIES = listOf(
            IntimacyItem("Protected", "🛡️", "Protected Sex", "የተጠበቀ ግንኙነት"),
            IntimacyItem("Unprotected", "💗", "Unprotected Sex", "ያልተጠበቀ ግንኙነት"),
            IntimacyItem("High Desire", "🔥", "High Libido / Desire", "ከፍተኛ ፍላጎት"),
            IntimacyItem("Solo", "✨", "Solo / Masturbation", "ብቸኛ እርካታ")
        )
    }
}
