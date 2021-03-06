package com.test.x.locationcodetest

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    companion object {
        val alarmFrequency = if (BuildConfig.DEBUG) {
            TimeUnit.MINUTES.toMillis(1)
        } else {
            TimeUnit.HOURS.toMillis(1)
        }
        const val LOCATION_PERMISSION_REQUEST = 5
    }

    lateinit var helloWorld: TextView
    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloWorld = findViewById(R.id.hello)
        // considering i'm going to use the legacy flag anyway I might as well just use the
        // deprecated method rather than write api version logic.
        helloWorld.text = Html.fromHtml(getString(R.string.hello_world))
        helloWorld.movementMethod = LinkMovementMethod.getInstance()
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST)
        }
    }

    override fun onNewIntent(intent: Intent) {
        sendBroadcast(buildRescheduleIntent(this))
        super.onNewIntent(intent)
    }
}
