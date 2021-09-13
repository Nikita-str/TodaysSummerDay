package com.endless_summer.todays_summer_day

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "MAIN_ACTIVITY")

        val intentFilter = IntentFilter(BROADCAST_REC)
        applicationContext.registerReceiver(SummerWidget.broadcast_rec, intentFilter)

        val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, FictionService::class.java)
        val service = PendingIntent.getService(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        service.send()
        am.setInexactRepeating(AlarmManager.RTC, SummerWidget.getCurMs() + MS_IN_SEC * 5, MS_IN_HOUR / 4, service)


        //setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        finish()
    }

}