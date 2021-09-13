package com.endless_summer.todays_summer_day

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*

class FictionService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        Log.i(TAG, "SERV:create")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.i(TAG, "SERV:destroy")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? { return null }

}