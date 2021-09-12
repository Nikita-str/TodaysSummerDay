package com.endless_summer.todays_summer_day

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

import android.widget.RemoteViews
import java.util.*
import android.os.Build
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.ComponentName
import android.content.IntentFilter
import android.widget.Toast

const val WIDGET_UPD = "ALARM_WIDGET_UPD";

class SummerWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, awm: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) updateAppWidget(context, awm, appWidgetId)
    }

    companion object AlarmHelper{
        private fun getAlarmIntent(ctx: Context): PendingIntent{
            val intent = Intent(ctx, SummerWidget::class.java)
            intent.action = WIDGET_UPD
            return PendingIntent.getBroadcast(ctx, 0, intent, 0)
        }

        fun alarmSub(ctx: Context) {
            val interval = AlarmManager.INTERVAL_DAY
            val alarm = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarm_intent = getAlarmIntent(ctx)
            alarm.setInexactRepeating(AlarmManager.RTC, MS_IN_SEC.toLong(), interval, alarm_intent)
        }

        fun alarmUnsub(ctx: Context) {
            (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(getAlarmIntent(ctx))
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        alarmSub(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        alarmUnsub(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(context == null || intent == null) return

        val action = intent.getAction()
        val is_upd = action.equals(WIDGET_UPD)
        val is_time_changed =
            action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED)
        if (is_upd || is_time_changed) {
            if(is_time_changed){
                // due to issue#6
                alarmUnsub(context)
                alarmSub(context)
            }

            val widget_mgr = AppWidgetManager.getInstance(context)
            val component_name = ComponentName(context.packageName, javaClass.name)
            val ids = widget_mgr.getAppWidgetIds(component_name)
            onUpdate(context, widget_mgr, ids)
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