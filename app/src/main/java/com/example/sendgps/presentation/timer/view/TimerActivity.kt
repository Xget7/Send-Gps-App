/*
 *
 *   Created Juan on 8/16/21, 10:08 AM
 *   Copyright Ⓒ 2021. All rights reserved Ⓒ 2021
 *   Last modified: 8/16/21, 10:07 AM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 *       ____       ______
 *     /  ∘" |     /\/\/\/\
 *     \____\ \_|\/\/\/\/\/|>
 *           \_____________|
 *            \_\_\    |_|_|
 * /
 */

package com.example.sendgps.presentation.timer.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.sendgps.R
import com.example.sendgps.domain.ForegroundServices.ForegroundOnlyLocationService
import com.example.sendgps.presentation.main.view.MainActivity
import com.example.sendgps.presentation.timer.timerlogic.SettingsActivity
import com.example.sendgps.presentation.timer.timerlogic.TimerExpiredReceiver
import com.example.sendgps.util.NotificationUtil
import com.example.sendgps.util.PrefUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.coroutines.*
import java.util.*

@Suppress("UnusedEquals")
class TimerActivity : MainActivity(), CoroutineScope {

    override lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override lateinit var locationRequest: LocationRequest

    companion object {

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }


        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState {
        Stopped, Running, Restart
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L

    private var timerState = TimerState.Stopped
    var secondsRemaining = 0L

    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "    Timer"

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        //buttons
        fab_start.setOnClickListener {
            if (checkLocationPermission()) {
                if (isLocationEnable() || checkForegroundPermissions()) {
                    startTimer()
                    startService(Intent(this, ForegroundOnlyLocationService::class.java))
                    timerState = TimerState.Running
                    updateButtons()
                    fab_start.visibility = View.INVISIBLE
                    txt_start.visibility = View.INVISIBLE

                } else {
                    Toast.makeText(this, "Please enable your Location", Toast.LENGTH_LONG).show()
                }
            } else {
                requestPermission()
            }
        }



        fab_restart.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Restart
            timer.start()

        }
        fab_stop.setOnClickListener {
            timer.cancel()
            stopService(Intent(this, ForegroundOnlyLocationService::class.java))
            onTimerFinished()
        }
    }

    private fun checkForegroundPermissions(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun initTimer() {

        timerState = PrefUtil.getTimerState(this)
        if (timerState == TimerState.Stopped) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }
        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Restart)
            PrefUtil.getSecondsRemaining(this)
        else {
            timerLengthSeconds
        }
        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0) {
            onTimerFinished()
        } else if (timerState == TimerState.Running)
            startTimer()


        updateButtons()
        updateCountDownUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimerLength()
        progress_countdown.progress = 0
        fab_start.visibility = View.VISIBLE
        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountDownUI()
    }

    private fun startTimer() {


        timerState == (TimerState.Running)

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountDownUI()
            }

            override fun onFinish() {
                onTimerFinished()

            }


        }.start()
    }


    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountDownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        tv_countdown.text = "$minutesUntilFinished:${
            if (secondsStr.length == 2) secondsStr
            else "0$secondsStr"
        }"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                fab_restart.isEnabled = true
                fab_stop.isEnabled = true

            }
            TimerState.Stopped -> {
                fab_restart.isEnabled = false
                fab_stop.isEnabled = true
            }
            TimerState.Restart -> {
                fab_restart.isEnabled = true
                fab_stop.isEnabled = true
            }
        }
    }


    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    //background
    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running) {
            timer.cancel()
            println("segundo plano creo")

            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}