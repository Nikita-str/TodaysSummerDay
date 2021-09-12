package com.endless_summer.todays_summer_day

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

import android.widget.RemoteViews
import java.util.*

import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class SummerWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, awm: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) updateAppWidget(context, awm, appWidgetId)
    }

    companion object HandlerHelper{
        fun getCurMs(): Long {
            val c = Calendar.getInstance()
            var ret: Long = c[Calendar.HOUR_OF_DAY].toLong()
            ret = ret * MIN_IN_HOUR + c[Calendar.MINUTE].toLong()
            ret = ret * SEC_IN_MIN + c[Calendar.SECOND].toLong()
            ret = ret * MS_IN_SEC + c[Calendar.MILLISECOND].toLong()
            return ret
        }
        var curRunnable : Runnable? = null
        var handler: Handler? = null
    }


    fun callUpdByCtx(ctx: Context) {
        val widget_mgr = AppWidgetManager.getInstance(ctx)
        val component_name = ComponentName(ctx.packageName, javaClass.name)
        val ids = widget_mgr.getAppWidgetIds(component_name)
        onUpdate(ctx, widget_mgr, ids)
    }

    fun subUpdByHandler(ctx: Context) {
        if(handler == null) handler = Handler(Looper.myLooper()!!)
        curRunnable = object : Runnable {
            override fun run() {
                //Toast.makeText(ctx, "UPD BY HANDLER", Toast.LENGTH_LONG).show()//TODO:DEL
                callUpdByCtx(ctx)
                subUpdByHandler(ctx)
            }
        }
        handler!!.postDelayed(curRunnable!!, MS_IN_SEC.toLong() + MS_IN_DAY - getCurMs())
    }

    fun unsubUpdByHandler(ctx: Context) {
        if(handler == null)return
        if(curRunnable != null)handler!!.removeCallbacks(curRunnable!!)
        handler = null // TODO:?
        curRunnable = null
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        subUpdByHandler(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        unsubUpdByHandler(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(context == null || intent == null) return

        val action = intent.getAction()
        val is_time_changed =
            action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED)
        if (is_time_changed) {
            //+ due to issue#6
            unsubUpdByHandler(context)
            subUpdByHandler(context)
            //- due ...

            callUpdByCtx(context)
        }
    }
}

val summerDate = SummerDate()

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.summer_widget)

    val sum_date = summerDate.GetCurSummerDate()

    val str_summer_date = "" + sum_date.monthDay + " " + sum_date.monthName.lowercase()
    val str_dos_postfix = " " + context.getString(R.string.day_of_summer_postfix)
    val str_day_of_summer = "" + sum_date.dayOfSummer + str_dos_postfix

    views.setTextViewText(R.id.cur_date, str_summer_date)
    views.setTextViewText(R.id.day_of_summer, str_day_of_summer)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}