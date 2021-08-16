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

package com.example.sendgps.presentation.contactChooser

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.view.View
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.sendgps.R
import com.example.sendgps.base.BaseActivity
import com.example.sendgps.presentation.main.view.MainActivity
import com.example.sendgps.presentation.timer.view.TimerActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_contact_chooser.*
import com.google.android.material.snackbar.Snackbar.make as make1

class contactChooser : BaseActivity() {

    val db = FirebaseFirestore.getInstance()
    val userId  = FirebaseAuth.getInstance().currentUser?.uid
    private var PERMISSION_ID = 423


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        et_pickContact.setOnClickListener {
            if (checkContactsPermission()){
                val i = Intent(Intent.ACTION_PICK)
                i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                startActivityForResult(i,111)
            }else{
                requestPermission()
            }

        }

    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),PERMISSION_ID

        )
    }

    private fun checkContactsPermission() : Boolean{
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    override fun getLayout(): Int {
        return R.layout.activity_contact_chooser
    }

    @SuppressLint("Recycle", "ShowToast")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val contactUri = data?.data ?: return
            val cols = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val rs = contentResolver.query(contactUri, cols, null, null, null)
            if (rs?.moveToFirst()!!) {
               val nameContact = rs.getString(0)
                val numContact = rs.getString(1)

                val contacts = hashMapOf(
                    "contactNum" to nameContact,
                    "contactName" to numContact
                )
                pb_contact_chooser.visibility = View.VISIBLE

                if (userId != null) {
                    db.collection("users")
                        .document(userId)
                        .update(contacts as Map<String, Any>)
                        .addOnSuccessListener {
                            val intent = Intent(this,TimerActivity::class.java)
                            Toast.makeText(this,"Contact saved",Toast.LENGTH_LONG).show()
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            pb_contact_chooser.visibility = View.INVISIBLE
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                }
            }

            }
        }
    }

