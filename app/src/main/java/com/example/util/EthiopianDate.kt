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
        val (gYear, gMonth, gDay) = jdnToGregorian(jdn)
        return LocalDate.of(gYear, gMonth, gDay)
    }

    companion object {
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
            val jdn = gregorianToJdn(localDate.year, localDate.monthValue, localDate.dayOfMonth)
            return jdnToEthiopian(jdn)
        }

        fun now(): EthiopianDate {
            return fromGregorian(LocalDate.now())
        }

        fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
            val a = (14 - month) / 12
            val y = year + 4800 - a
            val m = month + 12 * a - 3
            return day + (153L * m + 2) / 5 + 365L * y + (y / 4) - (y / 100) + (y / 400) - 32045L
        }

        fun jdnToEthiopian(jdn: Long): EthiopianDate {
            val r = (jdn - 1723856L) % 1461L
            val n = (r % 365L) + 365L * (r / 1460L)
            val year = 4L * ((jdn - 1723856L) / 1461L) + (r / 365L) - (r / 1460L)
            val month = (n / 30L) + 1L
            val day = (n % 30L) + 1L
            return EthiopianDate(year.toInt(), month.toInt(), day.toInt())
        }

        fun ethiopianToJdn(year: Int, month: Int, day: Int): Long {
            return 1723856L + 365L * (year - 1L) + (year / 4L) + 30L * (month - 1L) + day - 1L
        }

        fun jdnToGregorian(jdn: Long): Triple<Int, Int, Int> {
            val l = jdn + 68569L
            val n = (4L * l) / 146097L
            val l2 = l - (146097L * n + 3L) / 4L
            val i = (4000L * (l2 + 1L)) / 1461001L
            val l3 = l2 - (1461L * i) / 4L + 31L
            val j = (80L * l3) / 2447L
            val day = (l3 - (2447L * j) / 80L).toInt()
            val l4 = j / 11L
            val month = (j + 2L - 12L * l4).toInt()
            val year = (100L * (n - 49L) + i + l4).toInt()
            return Triple(year, month, day)
        }
    }
}
