package com.test.x.locationcodetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        val alarmFrequency = if (BuildConfig.DEBUG) {
            TimeUnit.MINUTES.toMillis(1)
        } else {
            TimeUnit.HOURS.toMillis(1)
        }
    }

    lateinit var helloWorld: TextView
    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "new Intent: " + intent.toUri(0))
        helloWorld = findViewById(R.id.hello)
        // considering i'm going to use the legacy flag anyway I might as well just use the
        // deprecated method rather than write api version logic.
        helloWorld.text = Html.fromHtml(getString(R.string.hello_world))
        helloWorld.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onNewIntent(intent: Intent) {
        val updateIntent = Intent().apply {
            action = ACTION_LOCATION_UPDATE
            setPackage("com.test.x.locationcodetest")
            putExtra(EXTRA_RESCHEDULE, true)
        }
        Log.d("MainActivity", updateIntent.toUri(0))
        sendBroadcast(updateIntent)
        super.onNewIntent(intent)
    }
}
