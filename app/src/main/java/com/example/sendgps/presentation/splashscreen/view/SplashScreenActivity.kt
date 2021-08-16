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

package com.example.sendgps.presentation.splashscreen.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.sendgps.R
import com.example.sendgps.base.BaseActivity
import com.example.sendgps.presentation.auth.login.view.LoginActivity
import com.example.sendgps.presentation.contactChooser.contactChooser
import com.example.sendgps.presentation.main.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

val userId  = FirebaseAuth.getInstance().currentUser?.uid


class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            val user = FirebaseAuth.getInstance().currentUser

            if(user != null) {
                startProfileActivity()
            } else {
                startLoginActivity()
            }
        },1500)

    }



    override fun getLayout(): Int {
        return R.layout.activity_splash_screen_activitty
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startProfileActivity() {
        val rootRef = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = rootRef.collection("users").document(userId!!)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    val contactNum = document.getString("contactNum")
                    if (contactNum != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }else{
                        val intent = Intent(this, contactChooser::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    print(task.exception)
                }
            }
        }
    }

}