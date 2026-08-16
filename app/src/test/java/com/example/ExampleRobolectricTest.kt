package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.EthiopianCalendarUtils
import com.example.util.EthiopianDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ethiopian Period Planner", appName)
    }

    @Test
    fun `ethiopian date conversion accuracy`() {
        // Known Ethiopian New Year (Meskerem 1, 2017 EC -> September 11, 2024 GC)
        val gregDate1 = LocalDate.of(2024, 9, 11)
        val ethDate1 = EthiopianDate.fromGregorian(gregDate1)
        assertEquals(2017, ethDate1.year)
        assertEquals(1, ethDate1.month)
        assertEquals(1, ethDate1.day)
        assertEquals(gregDate1, ethDate1.toGregorianLocalDate())

        // Leap Year New Year (Meskerem 1, 2016 EC -> September 12, 2023 GC)
        val gregDate2 = LocalDate.of(2023, 9, 12)
        val ethDate2 = EthiopianDate.fromGregorian(gregDate2)
        assertEquals(2016, ethDate2.year)
        assertEquals(1, ethDate2.month)
        assertEquals(1, ethDate2.day)
        assertEquals(gregDate2, ethDate2.toGregorianLocalDate())

        // Pagume 6 in a leap year (2015 EC was a leap year: Pagume 6, 2015 -> September 11, 2023 GC)
        val ethLeapPagume = EthiopianDate(2015, 13, 6)
        val gregLeapPagume = ethLeapPagume.toGregorianLocalDate()
        assertEquals(LocalDate.of(2023, 9, 11), gregLeapPagume)
        assertEquals(ethLeapPagume, EthiopianDate.fromGregorian(gregLeapPagume))

        // Check utility leap year validation
        assertTrue(EthiopianCalendarUtils.isEthiopianLeapYear(2015))
        assertEquals(6, EthiopianCalendarUtils.getDaysInEthiopianMonth(2015, 13))
        assertEquals(5, EthiopianCalendarUtils.getDaysInEthiopianMonth(2016, 13))
    }
}
