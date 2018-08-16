package com.test.x.locationcodetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

const val APP_PREFS = "com.test.x.locationcodetest.APP_PREFS"
const val SCHEDULE_ON_BOOT = "com.test.x.locationcodetest.SCHEDULE_ON_BOOT"

class BootReceiverScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE).getBoolean(SCHEDULE_ON_BOOT, false)) {
            scheduleLocationCheck(context, buildRescheduleIntent(context))
            Log.w(BootReceiverScheduler::class.java.simpleName, "Rescheduling after reboot!")
        }
    }
}