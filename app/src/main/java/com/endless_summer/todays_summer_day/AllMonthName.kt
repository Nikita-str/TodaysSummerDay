package com.endless_summer.todays_summer_day

import java.util.*
import kotlin.collections.ArrayList

const val MONTH_AMOUNT = 12

class AllMonthName(val locale: Locale = Locale.ROOT) {
    private val monthName = ArrayList<String>(MONTH_AMOUNT)
    private val badMonthInd = mutableSetOf<Int>()
    var badMonth = 0
        get
        protected set

    val isFullyGood get() = badMonth == 0
    val isFullyBad get() = badMonth == MONTH_AMOUNT

    fun GetName(month_id: Int) =
        if (month_id < 0 || MONTH_AMOUNT <= month_id) "" else monthName[month_id]

    fun IsGoodMonth(month_id: Int) = !IsBadMonth(month_id)
    fun IsBadMonth(month_id: Int) =
        if (month_id < 0 || MONTH_AMOUNT <= month_id) true else badMonthInd.contains(month_id)

    operator fun get(month_id: Int) : String = GetName(month_id)

    init {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        for (i in Calendar.JANUARY .. Calendar.DECEMBER) {
            calendar.set(Calendar.MONTH, i)
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
            if (month == null || month.length == 0) {
                badMonth++
                badMonthInd.add(i)
                monthName.add("")
            } else {
                monthName.add(month)
            }
        }
    }
}