package com.endless_summer.todays_summer_day

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

import android.widget.RemoteViews
import java.util.*
import android.os.Build




/**
 * Implementation of App Widget functionality.
 */
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
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun getCurrentLocale(context: Context): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.summer_widget)

    //val widgetText = context.getString(R.string.appwidget_text)
    val calendar = Calendar.getInstance()
    val cur_mounth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) // or last must be getCurrentLocale ?
    val widgetText = if(cur_mounth != null) cur_mounth.lowercase() else "TODO"
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}