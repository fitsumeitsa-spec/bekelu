package com.example.data

import com.example.model.DailyLogRecord
import com.example.model.PeriodRecord
import com.example.model.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

class PeriodRepository(private val db: AppDatabase) {
    private val periodDao = db.periodDao()
    private val dailyLogDao = db.dailyLogDao()
    private val userSettingsDao = db.userSettingsDao()

    val allPeriods: Flow<List<PeriodRecord>> = periodDao.getAllPeriods()
    val latestPeriod: Flow<PeriodRecord?> = periodDao.getLatestPeriod()
    val allDailyLogs: Flow<List<DailyLogRecord>> = dailyLogDao.getAllDailyLogs()
    val userSettings: Flow<UserSettingsEntity?> = userSettingsDao.getUserSettings()

    fun getDailyLog(date: String): Flow<DailyLogRecord?> = dailyLogDao.getDailyLogForDate(date)

    suspend fun getDailyLogDirect(date: String): DailyLogRecord? = dailyLogDao.getDailyLogDirect(date)

    suspend fun insertPeriod(period: PeriodRecord): Long = periodDao.insertPeriod(period)

    suspend fun updatePeriod(period: PeriodRecord) = periodDao.updatePeriod(period)

    suspend fun deletePeriod(id: Long) = periodDao.deletePeriodById(id)

    suspend fun saveDailyLog(log: DailyLogRecord) = dailyLogDao.insertOrUpdateDailyLog(log)

    suspend fun getUserSettingsDirect(): UserSettingsEntity? = userSettingsDao.getUserSettingsDirect()

    suspend fun saveUserSettings(settings: UserSettingsEntity) = userSettingsDao.insertOrUpdateSettings(settings)

    suspend fun clearAllData() {
        periodDao.deleteAllPeriods()
        dailyLogDao.deleteAllLogs()
        userSettingsDao.clearSettings()
    }
}
