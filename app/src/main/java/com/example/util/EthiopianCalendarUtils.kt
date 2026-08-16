package com.example.util

import java.time.LocalDate

/**
 * Utility object to handle conversion between Gregorian and Ethiopian dates,
 * calculating Ethiopian month grids, day name formatting, and calendar transformations.
 */
object EthiopianCalendarUtils {

    val MONTHS_EN = listOf(
        "Meskerem", "Tikimt", "Hidar", "Tahsas",
        "Tir", "Yakatit", "Megabit", "Miazia",
        "Ginbot", "Sene", "Hamle", "Nehase", "Pagume"
    )

    val MONTHS_AM = listOf(
        "መስከረም", "ጥቅምት", "ህዳር", "ታህሳስ",
        "ጥር", "የካቲት", "መጋቢት", "ሚያዚያ",
        "ግንቦት", "ሰኔ", "ሐምሌ", "ነሐሴ", "ጳጉሜ"
    )

    val DAYS_OF_WEEK_AM = listOf("ሰኞ", "ማክሰኞ", "ረቡዕ", "ሐሙስ", "አርብ", "ቅዳሜ", "እሁድ")
    val DAYS_OF_WEEK_SHORT_AM = listOf("ሰ", "ማ", "ረ", "ሐ", "አ", "ቅ", "እ")
    val DAYS_OF_WEEK_SHORT_EN = listOf("M", "T", "W", "T", "F", "S", "S")

    /**
     * Converts a Java [LocalDate] (Gregorian) into an [EthiopianDate].
     */
    fun toEthiopianDate(gregorianDate: LocalDate): EthiopianDate {
        return EthiopianDate.fromGregorian(gregorianDate)
    }

    /**
     * Converts an [EthiopianDate] back to a standard [LocalDate] (Gregorian).
     */
    fun toGregorianDate(ethiopianDate: EthiopianDate): LocalDate {
        return ethiopianDate.toGregorianLocalDate()
    }

    /**
     * Returns the current date in Ethiopian Calendar representation.
     */
    fun getTodayEthiopian(): EthiopianDate {
        return EthiopianDate.now()
    }

    /**
     * Calculates the number of days in a given Ethiopian year and month.
     * (Meskerem to Nehase = 30 days; Pagume = 5 days, or 6 days in a leap year).
     */
    fun getDaysInEthiopianMonth(year: Int, month: Int): Int {
        return when (month) {
            13 -> if (isEthiopianLeapYear(year)) 6 else 5
            else -> 30
        }
    }

    /**
     * Determines whether the specified Ethiopian year is a leap year (Puagme has 6 days).
     */
    fun isEthiopianLeapYear(year: Int): Boolean {
        return year % 4 == 3
    }

    /**
     * Returns the Ethiopian month name for a given month index (1 to 13).
     */
    fun getMonthName(month: Int, isAmharic: Boolean = false): String {
        val safeMonth = month.coerceIn(1, 13)
        return if (isAmharic) MONTHS_AM[safeMonth - 1] else MONTHS_EN[safeMonth - 1]
    }

    /**
     * Formats an [EthiopianDate] into a user-friendly display string.
     */
    fun formatEthiopianDate(ethiopianDate: EthiopianDate, isAmharic: Boolean = false): String {
        return ethiopianDate.format(isAmharic)
    }

    /**
     * Formats a Gregorian [LocalDate] into its Ethiopian formatted counterpart.
     */
    fun formatGregorianAsEthiopian(gregorianDate: LocalDate, isAmharic: Boolean = false): String {
        val ethDate = toEthiopianDate(gregorianDate)
        return formatEthiopianDate(ethDate, isAmharic)
    }

    /**
     * Converts Gregorian date components directly to Julian Day Number (JDN).
     */
    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        return EthiopianDate.gregorianToJdn(year, month, day)
    }

    /**
     * Converts Julian Day Number (JDN) to [EthiopianDate].
     */
    fun jdnToEthiopian(jdn: Long): EthiopianDate {
        return EthiopianDate.jdnToEthiopian(jdn)
    }

    /**
     * Converts Ethiopian date components directly to Julian Day Number (JDN).
     */
    fun ethiopianToJdn(year: Int, month: Int, day: Int): Long {
        return EthiopianDate.ethiopianToJdn(year, month, day)
    }

    /**
     * Converts Julian Day Number (JDN) to Gregorian (year, month, day).
     */
    fun jdnToGregorian(jdn: Long): Triple<Int, Int, Int> {
        return EthiopianDate.jdnToGregorian(jdn)
    }
}
