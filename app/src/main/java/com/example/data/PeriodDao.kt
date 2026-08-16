package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.DailyLogRecord
import com.example.model.PeriodRecord
import com.example.model.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY startDate DESC")
    fun getAllPeriods(): Flow<List<PeriodRecord>>

    @Query("SELECT * FROM periods ORDER BY startDate DESC")
    suspend fun getAllPeriodsDirect(): List<PeriodRecord>

    @Query("SELECT * FROM periods ORDER BY startDate DESC LIMIT 1")
    fun getLatestPeriod(): Flow<PeriodRecord?>

    @Query("SELECT * FROM periods WHERE id = :id")
    suspend fun getPeriodById(id: Long): PeriodRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodRecord): Long

    @Update
    suspend fun updatePeriod(period: PeriodRecord)

    @Query("DELETE FROM periods WHERE id = :id")
    suspend fun deletePeriodById(id: Long)

    @Query("DELETE FROM periods")
    suspend fun deleteAllPeriods()
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllDailyLogs(): Flow<List<DailyLogRecord>>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    fun getDailyLogForDate(date: String): Flow<DailyLogRecord?>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getDailyLogDirect(date: String): DailyLogRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyLog(log: DailyLogRecord)

    @Query("DELETE FROM daily_logs WHERE date = :date")
    suspend fun deleteDailyLog(date: String)

    @Query("DELETE FROM daily_logs")
    suspend fun deleteAllLogs()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettingsEntity)

    @Query("DELETE FROM user_settings")
    suspend fun clearSettings()
}
