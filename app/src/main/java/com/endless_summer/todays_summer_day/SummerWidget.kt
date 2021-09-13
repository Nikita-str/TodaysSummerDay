package com.endless_summer.todays_summer_day

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*

import android.widget.RemoteViews
import java.util.*

import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

const val TAG = "todays_summer_day"

class SummerWidget : AppWidgetProvider() {

    companion object HandlerHelper{
        var broadcast_rec = BroadRec()

        fun sendUpdIntent(ctx: Context){
            val intent = Intent(ctx, SummerWidget::class.java)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            val component_name = ComponentName(ctx.applicationContext, SummerWidget::class.java)
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
        var inf_work_id: UUID? = null
        const val UNIQUE_WORK = "upd_summer_day"
        const val UNIQUE_INF_WORK = "inf_upd_summer_day"

        fun sub(ctx: Context){
            val dt = MS_IN_SEC.toLong() + MS_IN_DAY - getCurMs()

            val work = OneTimeWorkRequestBuilder<UpdateWorker>()
                .setInitialDelay(dt, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(ctx.applicationContext)
                .enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.REPLACE,  work)
            work_id = work.id

            Log.i(TAG, "sub{ctx:$ctx dt:$dt w:$work}")
        }

        fun subInf(ctx: Context){
            alarmSub(ctx)

            val inf_work = PeriodicWorkRequestBuilder<InfUpdWorker>(
                MS_IN_DAY,
                TimeUnit.MILLISECONDS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS).setInitialDelay(
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS + MS_IN_MIN - getCurMs() - MS_IN_YEAR,
                TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(ctx.applicationContext)
                .enqueueUniquePeriodicWork(UNIQUE_INF_WORK, ExistingPeriodicWorkPolicy.REPLACE, inf_work)
            inf_work_id = inf_work.id
        }

        fun unsub(ctx: Context){
            Log.i(TAG, "unsub{ctx:$ctx}")
            val wid = work_id

            if(wid != null)WorkManager.getInstance(ctx).cancelWorkById(wid)
        }

        fun unsubInf(ctx: Context){
            alarmUnsub(ctx)

            val wid = inf_work_id
            if(wid != null)WorkManager.getInstance(ctx).cancelWorkById(wid)
        }

        fun staticOnReceive(context: Context?, intent: Intent?) {
            if (context == null || intent == null) return

            val action = intent.getAction()
            val is_time_changed =
                action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED)
            Log.i(TAG, "receive[ACT]{$action}  ok:$is_time_changed")
            if (is_time_changed) {
                //+ due to issue#6
                //unsub(context)
                sub(context)
                //- due ...

                callUpdByCtx(context)
            }
        }

        private fun getAlarmIntent(ctx: Context): PendingIntent {
            val intent = Intent(ctx, SummerWidget::class.java)
            intent.setAction(Intent.ACTION_TIME_CHANGED)
            return PendingIntent.getBroadcast(ctx, 0, intent, 0)
        }

        private fun alarmSub(ctx: Context) {
            val interval = AlarmManager.INTERVAL_DAY
            val alarm = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarm_intent = getAlarmIntent(ctx)
            alarm.setInexactRepeating(AlarmManager.RTC, MS_IN_SEC.toLong(), interval, alarm_intent)
        }

        private fun alarmUnsub(ctx: Context) {
            (ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(getAlarmIntent(ctx))
        }
    }

    override fun onUpdate(ctx: Context, awMan: AppWidgetManager, awIds: IntArray) =
        staticUpdate(ctx, awMan, awIds)

    override fun onEnabled(context: Context) {
        Log.i(TAG, "on_enabled")
        sub(context)
        subInf(context)

        try {
            val infi = IntentFilter(Intent.ACTION_TIME_CHANGED)
            context.applicationContext.registerReceiver(broadcast_rec, infi)
        } catch(e: Exception){
            Log.i(TAG, "ERR:on_enable:$e")
        }

        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        Log.i(TAG, "on_enabled")
        unsub(context)
        unsubInf(context)

        try {
            context.unregisterReceiver(broadcast_rec)
        } catch(e: Exception){
            Log.i(TAG, "ERR:on_disable:$e")
        }

        super.onDisabled(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "receive{ctx:$context intent:$intent}")
        super.onReceive(context, intent)
        staticOnReceive(context, intent)
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