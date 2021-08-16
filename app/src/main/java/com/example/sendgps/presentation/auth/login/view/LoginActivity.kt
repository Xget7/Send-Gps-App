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

package com.example.sendgps.presentation.auth.login.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.sendgps.R
import com.example.sendgps.SendGpsApp
import com.example.sendgps.base.BaseActivity
import com.example.sendgps.presentation.auth.login.LoginContract
import com.example.sendgps.presentation.auth.login.presenter.LoginPresenter
import com.example.sendgps.presentation.auth.passwordRecover.view.PasswordRecover
import com.example.sendgps.presentation.auth.register.view.RegisterActivity
import com.example.sendgps.presentation.contactChooser.contactChooser
import com.example.sendgps.presentation.main.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : BaseActivity(), LoginContract.LoginView {

    @Inject
    lateinit var presenter: LoginPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as SendGpsApp).getAppComponent()
            ?.injectLog(this)//different for other activities
        presenter.attachView(this)


        //buttons
        btn_signIn.setOnClickListener {
            signIn()
        }

        dont_have_account.setOnClickListener {
            navigateToRegister()
        }

        txt_password_recover.setOnClickListener {
            navigateToPasswordRecover()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun showError(msgError: String?) {
        toast(this, msgError)
    }

    override fun showProgressBar() {
        progressBar_SignIn.visibility = View.VISIBLE
        btn_signIn.visibility = View.GONE
    }

    override fun hideProgressBar() {
        progressBar_SignIn.visibility = View.GONE
        btn_signIn.visibility = View.VISIBLE

    }

    override fun signIn() {
        val email = etxt_email.text.toString().trim()
        val password = etxt_password.text.toString().trim()
        if (presenter.checkEmptyFields(email, password)) toast(this, "One or two fields are empty")
        else presenter.signInUserWithEmailAndPassword(email, password)
    }

    override fun navigateToMain() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
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
                        finish()
                    } else {
                        val intent = Intent(this, contactChooser::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.exception)
                }
            }
        }
    }


    override fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun navigateToPasswordRecover() {
        startActivity(Intent(this, PasswordRecover::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
        presenter.detachJob()
    }


}