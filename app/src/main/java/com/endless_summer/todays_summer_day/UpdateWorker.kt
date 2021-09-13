package com.endless_summer.todays_summer_day

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateWorker(val ctx: Context, wParams: WorkerParameters) : Worker(ctx, wParams) {
    companion object {
        const val MAX_RETRY_TIMES = 5
        var times_of_retry = 0
    }

    override fun doWork(): Result {
        try {
            Log.i(TAG, "do_work[$times_of_retry]{ctx:$ctx}")
            times_of_retry = 0
            SummerWidget.sendUpdIntent(ctx)
            SummerWidget.sub(ctx)
        } catch (e: Exception) {
            Log.i(TAG, "EXC:do_work[$times_of_retry]{EXC: $e}")
            return if (times_of_retry < MAX_RETRY_TIMES) Result.retry() else Result.failure()
        }
        return Result.success()
    }
}