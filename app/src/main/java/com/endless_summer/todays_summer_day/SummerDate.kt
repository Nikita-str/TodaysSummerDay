package com.endless_summer.todays_summer_day

import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

const val BAD_MONTH_NAME = "Summer!"

const val MS_IN_SEC = 1000
const val SEC_IN_MIN = 60
const val MIN_IN_HOUR = 60
const val HOUR_IN_DAY = 24

class SummerDate {
    protected val engMonthName = AllMonthName(Locale.ENGLISH)
    protected var curMonthName = AllMonthName(Locale.getDefault())

    fun GetCurrentMonth() = Calendar.getInstance().get(Calendar.MONTH)
    fun GetCurrentDay() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    fun MonthAfterSummer(month_ind: Int = GetCurrentMonth()) = Calendar.AUGUST < month_ind
    fun MonthIsSummer(month_ind: Int = GetCurrentMonth()) =
        Calendar.JUNE <= month_ind && month_ind <= Calendar.AUGUST

    data class SumDate(val monthName : String, val monthDay: Int)
    fun GetCurSummerDate() : SumDate{
        if (curMonthName.locale != Locale.getDefault())
            curMonthName = AllMonthName(Locale.getDefault())

        return SumDate(GetCurMonthName(), GetCurMonthDay())
    }

    fun GetCurMonthName() : String {
        var cur_month_ind = GetCurrentMonth()
        if (!MonthIsSummer(cur_month_ind)) cur_month_ind = Calendar.AUGUST

        return if (curMonthName.IsGoodMonth(cur_month_ind)) curMonthName[cur_month_ind]
        else if (engMonthName.IsGoodMonth(cur_month_ind)) engMonthName[cur_month_ind]
        else BAD_MONTH_NAME
    }

    private fun SetLastToMin(c: Calendar){
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY))
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE))
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND))
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND))
    }
    private fun DayBetweenCalend(c_from: Calendar, c_to: Calendar) =
        DeltaTimeToDay(c_to.getTime().getTime() + MS_IN_SEC - c_from.getTime().getTime())
    private fun DeltaTimeToDay(dt: Long) : Int =
        ((((dt / MS_IN_SEC) / SEC_IN_MIN) / MIN_IN_HOUR) / HOUR_IN_DAY).toInt()

    fun GetCurMonthDay() : Int{
        val cur_month_ind = GetCurrentMonth()
        if(MonthIsSummer(cur_month_ind)) return GetCurrentDay()

        val c0 = Calendar.getInstance()
        if(!MonthAfterSummer(cur_month_ind)) c0.set(Calendar.YEAR, c0.get(Calendar.YEAR) - 1)
        c0.set(Calendar.DAY_OF_MONTH, 1)
        c0.set(Calendar.MONTH, Calendar.AUGUST)
        c0.set(Calendar.DAY_OF_MONTH, c0.getActualMaximum(Calendar.DAY_OF_MONTH))
        SetLastToMin(c0)

        val c1 = Calendar.getInstance()
        SetLastToMin(c1)
        
        return DayBetweenCalend(c0 ,c1)
    }


}