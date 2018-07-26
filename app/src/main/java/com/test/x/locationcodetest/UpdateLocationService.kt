package com.test.x.locationcodetest

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val APP_PREFS = "com.test.x.locationcodetest.APP_PREFS"
const val SCHEDULE_ON_BOOT = "com.test.x.locationcodetest.SCHEDULE_ON_BOOT"
const val EXTRA_RESCHEDULE = "com.test.x.locationcodetest.EXTRA_RESCHEDULE"

fun buildRescheduleIntent(context: Context) : Intent {
   return Intent(context, UpdateLocationService::class.java).apply {
        putExtra(EXTRA_RESCHEDULE, true)
    }
}

/**
 * This will check the intent for the EXTRA_RESCHEDULE flag and schedule or reschedule the alarm
 */
fun scheduleLocationCheck(context: Context, intent: Intent) {
    if (intent.hasExtra(EXTRA_RESCHEDULE)) {
        // to prevent endless rescheduling the EXTRA_RESCHEDULE and all other extras are
        // stripped from the intent.
        intent.replaceExtras(Bundle())
        val pendingIntent : PendingIntent? = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        // I considered using the androidx WorkManager
        // (https://developer.android.com/topic/libraries/architecture/workmanager)
        // but as it was in beta I decided to stick with the old AlarmManager as it still works
        // with all the api versions mandated by the project requirements.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + UpdateLocationService.alarmFrequency,
                UpdateLocationService.alarmFrequency,
                pendingIntent)
        Log.d(
               "scheduleLocationCheck",
                context.getString(R.string.work_scheuled,
                        UpdateLocationService.alarmFrequency.toString()))

        context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SCHEDULE_ON_BOOT, true)
                .apply()
    }
}

class UpdateLocationService : Service() {

    companion object {
        val alarmFrequency = if (BuildConfig.DEBUG) {
            TimeUnit.MINUTES.toMillis(1)
        } else {
            TimeUnit.HOURS.toMillis(1)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        // This service only exists to do work and does not need to be bound to.
        return null

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        scheduleLocationCheck(this, intent)
        doLocationWork()
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Using google play services here as it's somewhat more convent. There should be a check
     * when the app first launches to verify that play services are available on the device as
     * this would not work on for amazon fire devices. If play services are not found to be
     * installed the app should fall back to a solution using the os api.
     */
    private fun doLocationWork() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val msg =  getString(R.string.time_toast,
                            SimpleDateFormat.getTimeInstance().format(Calendar.getInstance().time),
                            location.toString())
                    Log.d(UpdateLocationService::class.java.simpleName, msg)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    // TODO: Storage options: Really the only options are to write to a file or use
                    // a sql lite database. Don't even try arguing that shared preferences is a
                    // valid solution.
                    stopSelf()
                }
            }
        }

    }
}