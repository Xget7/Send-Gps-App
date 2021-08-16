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

package com.example.sendgps.domain.ForegroundServices

import android.Manifest
import android.app.*
import android.app.NotificationChannel.DEFAULT_CHANNEL_ID
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.telephony.SmsManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.example.sendgps.presentation.timer.timerlogic.TimerExpiredReceiver
import com.example.sendgps.presentation.timer.view.TimerActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class ForegroundOnlyLocationService() : Service() {


    companion object {//minutes


        private const val SECONDS_REMAINING_ID = "com.example.sendgps.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }


    }


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID = 423
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Background Process", Toast.LENGTH_SHORT).show()
        var secondsRemaining = getSecondsRemaining(this) * 1000
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        GlobalScope.launch {
            Thread.sleep(secondsRemaining)
            getLastLocation()
        }
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getLastLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermission()) {
            //check permission
            if (isLocationEnable()) {
                //get Location
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        getFbData(location.latitude, location.longitude)
                    }
                }
            }
        } else {
            requestPermission()
        }

    }

    private fun getFbData(latitude: Double, longitude: Double) {
        val docRef = db.collection("users").document(uid)
        docRef.get().addOnSuccessListener {
            if (it.exists()) {
                val num = it.getString("contactNum").toString()
                val smsManager = SmsManager.getDefault() as SmsManager
                smsManager.sendTextMessage(
                    num,
                    null,
                    "http://maps.google.com/?q=" + latitude + "," + longitude,
                    null,
                    null
                )
            }
        }
    }


    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            //set the new location
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //Request
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            applicationContext as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
            ), PERMISSION_ID

        )
    }

    //check location service function
    private fun isLocationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

}

