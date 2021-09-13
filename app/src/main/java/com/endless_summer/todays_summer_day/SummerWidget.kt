package com.endless_summer.todays_summer_day

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

import android.widget.RemoteViews
import java.util.*

import android.content.ComponentName
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Chronometer
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.contracts.contract

const val TAG = "todays_summer_day"
const val BROADCAST_REC = "todays_summer_day_BROADCAST"

class SummerWidget : AppWidgetProvider() {

    override fun onUpdate(ctx: Context, awMan: AppWidgetManager, awIds: IntArray) =
        staticUpdate(ctx, awMan, awIds)

    override fun onAppWidgetOptionsChanged(ctx: Context?, awMan: AppWidgetManager?, awId: Int, bundle: Bundle?) {
        Log.i(TAG, "on_aw_options_changed")
        super.onAppWidgetOptionsChanged(ctx, awMan, awId, bundle)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        Log.i(TAG, "on_restored")
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    companion object HandlerHelper{

        var broadcast_rec = BroadRec()

        fun sendUpdIntent(ctx: Context){
            val intent = Intent(ctx, SummerWidget::class.java)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            val component_name = ComponentName(ctx.packageName, SummerWidget::class.java.name)
            val ids = AppWidgetManager.getInstance(ctx).getAppWidgetIds(component_name)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            ctx.sendBroadcast(intent)
        }

        fun staticUpdate(ctx: Context, awMan: AppWidgetManager, awIds: IntArray) {
            Log.i(TAG, "UPD{ctx:$ctx ids:$awIds}")
            for (aw_id in awIds) updateAppWidget(ctx, awMan, aw_id)
        }

        fun callUpdByCtx(ctx: Context) {
            val widget_mgr = AppWidgetManager.getInstance(ctx)
            val component_name = ComponentName(ctx.packageName, SummerWidget::class.java.name)
            val ids = widget_mgr.getAppWidgetIds(component_name)
            staticUpdate(ctx, widget_mgr, ids)
        }

        fun getCurMs(): Long {
            val c = Calendar.getInstance()
            var ret: Long = c[Calendar.HOUR_OF_DAY].toLong()
            ret = ret * MIN_IN_HOUR + c[Calendar.MINUTE].toLong()
            ret = ret * SEC_IN_MIN + c[Calendar.SECOND].toLong()
            ret = ret * MS_IN_SEC + c[Calendar.MILLISECOND].toLong()
            return ret
        }

        var work_id: UUID? = null

        fun sub(ctx: Context){
            val dt = MS_IN_SEC.toLong() + MS_IN_DAY - getCurMs()
            val work = OneTimeWorkRequestBuilder<UpdateWorker>().setInitialDelay(dt, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(ctx).enqueue(work)

            val inf = PeriodicWorkRequestBuilder<UpdateWorker>(MS_IN_DAY, TimeUnit.MILLISECONDS)
                .setInitialDelay(dt, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(ctx.applicationContext).enqueue(inf)

            work_id = work.id
            Log.i(TAG, "sub{ctx:$ctx dt:$dt w:$work}")
        }

        fun unsub(ctx: Context){
            Log.i(TAG, "unsub{ctx:$ctx}")
            val wid = work_id
            if(wid != null)WorkManager.getInstance(ctx).cancelWorkById(wid)
        }
    }

    override fun onEnabled(context: Context) {
        Log.i(TAG, "on_enabled")
        sub(context)

        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.i(TAG, "on_enabled")
        unsub(context)
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Log.i(TAG, "deleted{ctx:$context}")
        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        Log.i(TAG, "receive{ctx:$context intent:$intent}")
        if(context == null || intent == null) return

        val action = intent.getAction()
        val is_time_changed =
            action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED)
        Log.i(TAG, "receive[ACT]{$action}  ok:$is_time_changed")
        if (is_time_changed) {
            //+ due to issue#6
            unsub(context)
            sub(context)
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

    //val c = Chronometer(context)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}