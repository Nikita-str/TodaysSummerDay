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
import android.widget.Toast


const val WIDGET_UPD = "ALARM_WIDGET_UPD";

class SummerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val intent = Intent(WIDGET_UPD)
        val alarm_intent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val c = Calendar.getInstance()
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 100

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(AlarmManager.RTC, c.timeInMillis, AlarmManager.INTERVAL_DAY, alarm_intent)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        val intent = Intent(WIDGET_UPD)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(PendingIntent.getBroadcast(context, 0, intent, 0))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(context == null) return

        if (intent != null && intent.getAction().equals(WIDGET_UPD)) {
            Toast.makeText(context, WIDGET_UPD, Toast.LENGTH_LONG).show() //TODO:DEL

            val widget_mgr = AppWidgetManager.getInstance(context)
            val component_name = ComponentName(context.packageName, javaClass.name)
            val ids = widget_mgr.getAppWidgetIds(component_name)
            onUpdate(context, widget_mgr, ids)
        }
    }
}

fun getCurrentLocale(context: Context): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
}

val summerDate = SummerDate()

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.summer_widget)

    val sum_date = summerDate.GetCurSummerDate()
    views.setTextViewText(R.id.cur_date, "" + sum_date.monthDay + " " + sum_date.monthName.lowercase())
    views.setTextViewText(R.id.day_of_summer, "" + sum_date.dayOfSummer + " " + context.getString(R.string.day_of_summer_postfix))

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}