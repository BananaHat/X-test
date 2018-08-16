package com.test.x.locationcodetest

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

const val ACTION_LOCATION_UPDATE = "com.test.x.locationcodetest.action.LOCATION_UPDATE"
const val EXTRA_RESCHEDULE = "com.test.x.locationcodetest.EXTRA_RESCHEDULE"

fun buildRescheduleIntent(context: Context) : Intent {
    return Intent().apply {
        action = ACTION_LOCATION_UPDATE
        setPackage("com.test.x.locationcodetest")
        putExtra(EXTRA_RESCHEDULE, true)
    }
}

fun scheduleLocationCheck(context: Context, intent: Intent) {
    if (intent.hasExtra(EXTRA_RESCHEDULE)) {
        intent.replaceExtras(Bundle())
        val pendingIntent : PendingIntent? = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        Log.d("UpdateLocationReceiver", MainActivity.alarmFrequency.toString())
        // I considered using the androidx WorkManager
        // (https://developer.android.com/topic/libraries/architecture/workmanager)
        // but as it was in beta I decided to stick with the old AlarmManager as it still works
        // with all the api versions mandated by the project requirements.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + MainActivity.alarmFrequency,
                MainActivity.alarmFrequency,
                pendingIntent)
        Log.d("UpdateLocationReceiver", context.getString(R.string.work_scheuled))
        Toast.makeText(context, context.getString(R.string.work_scheuled), Toast.LENGTH_LONG)
                .show()
    }
}

class UpdateLocationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("UpdateLocationReceiver", "received location broadcast")
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED) {
            val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val msg = context.getString(R.string.time_toast,
                    SimpleDateFormat.getTimeInstance().format(Calendar.getInstance().time),
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString())
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            Log.d("UpdateLocationReceiver", msg)
        }
        scheduleLocationCheck(context, intent)
    }
}