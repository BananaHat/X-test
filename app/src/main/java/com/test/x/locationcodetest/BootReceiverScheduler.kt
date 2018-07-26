package com.test.x.locationcodetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiverScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE).getBoolean(SCHEDULE_ON_BOOT, false)) {
            scheduleLocationCheck(context, buildRescheduleIntent(context))
            Log.w(BootReceiverScheduler::class.java.simpleName, "Rescheduling after reboot!")
        }
    }
}