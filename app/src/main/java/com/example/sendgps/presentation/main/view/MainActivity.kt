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

package com.example.sendgps.presentation.main.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.sendgps.R
import com.example.sendgps.presentation.auth.login.view.LoginActivity
import com.example.sendgps.presentation.contactChooser.contactChooser
import com.example.sendgps.presentation.timer.view.TimerActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    open lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    open lateinit var locationRequest: LocationRequest

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    //id unique
    private var PERMISSION_ID = 423

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Add Event to button
        goTimer.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        getLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        ChangeContact.setOnClickListener{
            val intent = Intent(this, contactChooser::class.java)
            startActivity(intent)
        }
    }




    open fun getFbData(latitude: Double, longitude: Double){
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
            } else {
                Toast.makeText(applicationContext, " Not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    open fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
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
                locationRequest,locationCallback,Looper.myLooper()
        )
    }

    private val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
        }
    }

    open fun checkLocationPermission() : Boolean{
        if (
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                ){
            return true
        }
        return false
    }

    //Request
    open fun requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.FOREGROUND_SERVICE),PERMISSION_ID

        )
    }

    //check location service function
    open fun isLocationEnable():Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check the permission result
        if (requestCode == PERMISSION_ID){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug","Permission OK")
            }
        }
    }

}