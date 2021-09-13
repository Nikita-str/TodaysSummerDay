package com.endless_summer.todays_summer_day

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BroadRec : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        Log.i(TAG, "BR:on_rec{int:$intent}")
        if(p0 == null)return
        SummerWidget.callUpdByCtx(p0)
    }
}