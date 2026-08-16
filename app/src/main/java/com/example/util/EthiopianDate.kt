package com.example.util

import java.time.LocalDate

data class EthiopianDate(
    val year: Int,
    val month: Int, // 1 to 13
    val day: Int    // 1 to 30 (or 1 to 5/6 for Pagume)
) {
    val isLeapYear: Boolean
        get() = year % 4 == 3

    val maxDaysInMonth: Int
        get() = when (month) {
            13 -> if (isLeapYear) 6 else 5
            else -> 30
        }

    fun getMonthName(isAmharic: Boolean = false): String {
        return if (isAmharic) {
            ETHIOPIAN_MONTHS_AM[month - 1]
        } else {
            ETHIOPIAN_MONTHS_EN[month - 1]
        }
    }

    fun format(isAmharic: Boolean = false): String {
        val monthName = getMonthName(isAmharic)
        return if (isAmharic) {
            "$day $monthName $year"
        } else {
            "$monthName $day, $year"
        }
    }

    fun toGregorianLocalDate(): LocalDate {
        val jdn = ethiopianToJdn(year, month, day)
        return LocalDate.ofEpochDay(jdn - JDN_UNIX_EPOCH)
    }

    companion object {
        const val JDN_OFFSET = 1723856L
        const val JDN_UNIX_EPOCH = 2440588L

        val ETHIOPIAN_MONTHS_EN = listOf(
            "Meskerem", "Tikimt", "Hidar", "Tahsas",
            "Tir", "Yakatit", "Megabit", "Miazia",
            "Ginbot", "Sene", "Hamle", "Nehase", "Pagume"
        )

        val ETHIOPIAN_MONTHS_AM = listOf(
            "መስከረም", "ጥቅምት", "ህዳር", "ታህሳስ",
            "ጥር", "የካቲት", "መጋቢት", "ሚያዚያ",
            "ግንቦት", "ሰኔ", "ሐምሌ", "ነሐሴ", "ጳጉሜ"
        )

        val ETHIOPIAN_DAYS_AM = listOf("ሰኞ", "ማክሰኞ", "ረቡዕ", "ሐሙስ", "አርብ", "ቅዳሜ", "እሁድ")
        val ETHIOPIAN_DAYS_SHORT_AM = listOf("ሰ", "ማ", "ረ", "ሐ", "አ", "ቅ", "እ")
        val DAYS_SHORT_EN = listOf("M", "T", "W", "T", "F", "S", "S")

        fun fromGregorian(localDate: LocalDate): EthiopianDate {
            val jdn = localDate.toEpochDay() + JDN_UNIX_EPOCH
            return jdnToEthiopian(jdn)
        }

        fun now(): EthiopianDate {
            return fromGregorian(LocalDate.now())
        }

        fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
            val localDate = LocalDate.of(year, month, day)
            return localDate.toEpochDay() + JDN_UNIX_EPOCH
        }

        fun jdnToEthiopian(jdn: Long): EthiopianDate {
            val r = (jdn - JDN_OFFSET) % 1461L
            val n = (r % 365L) + 365L * (r / 1460L)
            val year = 4L * ((jdn - JDN_OFFSET) / 1461L) + (r / 365L) - (r / 1460L)
            val month = (n / 30L) + 1L
            val day = (n % 30L) + 1L
            return EthiopianDate(year.toInt(), month.toInt(), day.toInt())
        }

        fun ethiopianToJdn(year: Int, month: Int, day: Int): Long {
            return JDN_OFFSET + 1461L * (year.toLong() / 4L) + 365L * (year.toLong() % 4L) + 30L * (month.toLong() - 1L) + day.toLong() - 1L
        }

        fun jdnToGregorian(jdn: Long): Triple<Int, Int, Int> {
            val localDate = LocalDate.ofEpochDay(jdn - JDN_UNIX_EPOCH)
            return Triple(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        }
    }
}
