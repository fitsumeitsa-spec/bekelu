package com.example.util

import com.example.model.CyclePhase
import com.example.model.CycleStatus
import com.example.model.PeriodRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.sqrt

object CycleEngine {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun calculateCycleStatus(
        periods: List<PeriodRecord>,
        userCycleLength: Int = 28,
        userPeriodLength: Int = 5,
        today: LocalDate = LocalDate.now()
    ): CycleStatus {
        val sortedPeriods = periods.mapNotNull { p ->
            try {
                val start = LocalDate.parse(p.startDate, dateFormatter)
                val end = p.endDate?.let { LocalDate.parse(it, dateFormatter) }
                Triple(p, start, end)
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.second }

        val calculatedCycleLength: Int
        val calculatedPeriodLength: Int
        val regularity: String

        if (sortedPeriods.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 0 until sortedPeriods.size - 1) {
                val current = sortedPeriods[i].second
                val previous = sortedPeriods[i + 1].second
                val diff = ChronoUnit.DAYS.between(previous, current)
                if (diff in 18..60) {
                    intervals.add(diff)
                }
            }
            calculatedCycleLength = if (intervals.isNotEmpty()) {
                intervals.average().toInt().coerceIn(21, 45)
            } else {
                userCycleLength
            }

            val durations = sortedPeriods.mapNotNull { (_, start, end) ->
                if (end != null) {
                    val len = ChronoUnit.DAYS.between(start, end) + 1
                    if (len in 2..12) len else null
                } else null
            }
            calculatedPeriodLength = if (durations.isNotEmpty()) {
                durations.average().toInt().coerceIn(3, 9)
            } else {
                userPeriodLength
            }

            if (intervals.size >= 2) {
                val mean = intervals.average()
                val variance = intervals.sumOf { (it - mean) * (it - mean) } / intervals.size
                val stdDev = sqrt(variance)
                regularity = when {
                    stdDev <= 2.0 -> "Regular"
                    stdDev <= 4.5 -> "Slightly Irregular"
                    else -> "Irregular"
                }
            } else {
                regularity = "Regular"
            }
        } else if (sortedPeriods.size == 1) {
            val (_, start, end) = sortedPeriods[0]
            val duration = if (end != null) {
                (ChronoUnit.DAYS.between(start, end) + 1).toInt().coerceIn(3, 9)
            } else {
                userPeriodLength
            }
            calculatedCycleLength = userCycleLength
            calculatedPeriodLength = duration
            regularity = "Regular"
        } else {
            // Default reference cycle for first launch
            calculatedCycleLength = userCycleLength
            calculatedPeriodLength = userPeriodLength
            regularity = "Regular"
        }

        val lastPeriodStart: LocalDate = if (sortedPeriods.isNotEmpty()) {
            sortedPeriods[0].second
        } else {
            // Reference baseline for initial state (e.g. Day 22)
            today.minusDays(21)
        }

        val daysSinceStart = ChronoUnit.DAYS.between(lastPeriodStart, today).toInt()
        val currentCycleDay = (daysSinceStart % calculatedCycleLength) + 1

        val expectedNextDate = lastPeriodStart.plusDays(calculatedCycleLength.toLong())
        val daysUntilNext = ChronoUnit.DAYS.between(today, expectedNextDate).toInt()

        val isPeriodActiveToday: Boolean = if (sortedPeriods.isNotEmpty()) {
            val latest = sortedPeriods[0]
            val start = latest.second
            val end = latest.third
            if (end != null) {
                !today.isBefore(start) && !today.isAfter(end)
            } else {
                val daysActive = ChronoUnit.DAYS.between(start, today)
                daysActive in 0 until calculatedPeriodLength
            }
        } else {
            false
        }

        val ovulationDate = expectedNextDate.minusDays(14)
        val fertileStart = ovulationDate.minusDays(5)
        val fertileEnd = ovulationDate.plusDays(1)

        val isOvulationDayToday = today == ovulationDate
        val isFertileWindowToday = !today.isBefore(fertileStart) && !today.isAfter(fertileEnd)

        val phase: CyclePhase = when {
            isPeriodActiveToday || currentCycleDay <= calculatedPeriodLength -> CyclePhase.MENSTRUAL
            isOvulationDayToday -> CyclePhase.OVULATION
            isFertileWindowToday -> CyclePhase.OVULATION
            currentCycleDay < calculatedCycleLength - 14 -> CyclePhase.FOLLICULAR
            else -> CyclePhase.LUTEAL
        }

        return CycleStatus(
            currentCycleDay = currentCycleDay,
            totalCycleLength = calculatedCycleLength,
            periodLength = calculatedPeriodLength,
            phase = phase,
            daysUntilNextPeriod = daysUntilNext,
            expectedNextPeriodDate = expectedNextDate,
            isPeriodActiveToday = isPeriodActiveToday,
            isOvulationDayToday = isOvulationDayToday,
            isFertileWindowToday = isFertileWindowToday,
            regularity = regularity,
            lastPeriodStartDate = if (sortedPeriods.isNotEmpty()) sortedPeriods[0].second else null
        )
    }

    fun isDateInPeriod(date: LocalDate, periods: List<PeriodRecord>, avgPeriodLength: Int = 5): Boolean {
        for (p in periods) {
            try {
                val start = LocalDate.parse(p.startDate, dateFormatter)
                val end = p.endDate?.let { LocalDate.parse(it, dateFormatter) } ?: start.plusDays((avgPeriodLength - 1).toLong())
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    return true
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
        return false
    }

    fun isDatePredictedPeriod(
        date: LocalDate,
        lastPeriodStart: LocalDate?,
        cycleLength: Int,
        periodLength: Int,
        futureCycles: Int = 12
    ): Boolean {
        val baseStart = lastPeriodStart ?: LocalDate.now().minusDays(21)
        for (i in 1..futureCycles) {
            val predictedStart = baseStart.plusDays((cycleLength * i).toLong())
            val predictedEnd = predictedStart.plusDays((periodLength - 1).toLong())
            if (!date.isBefore(predictedStart) && !date.isAfter(predictedEnd)) {
                return true
            }
        }
        return false
    }

    fun isDateFertile(
        date: LocalDate,
        lastPeriodStart: LocalDate?,
        cycleLength: Int,
        futureCycles: Int = 12
    ): Boolean {
        val baseStart = lastPeriodStart ?: LocalDate.now().minusDays(21)
        for (i in 0..futureCycles) {
            val nextStart = baseStart.plusDays((cycleLength * (i + 1)).toLong())
            val ovulation = nextStart.minusDays(14)
            val fertileStart = ovulation.minusDays(5)
            val fertileEnd = ovulation.plusDays(1)
            if (!date.isBefore(fertileStart) && !date.isAfter(fertileEnd)) {
                return true
            }
        }
        return false
    }
}
