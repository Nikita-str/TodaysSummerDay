package com.endless_summer.todays_summer_day

import org.junit.Test

import org.junit.Assert.*
import java.io.Console

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun temp_test() {
        val x = SummerDate().GetCurSummerDate()
        println("" + x.monthDay + " " + x.monthName + ";  " + x.dayOfSummer + " день лета")
    }
}