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

package com.example.sendgps.presentation.auth.register.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.sendgps.R
import com.example.sendgps.SendGpsApp
import com.example.sendgps.base.BaseActivity
import com.example.sendgps.presentation.auth.login.view.LoginActivity
import com.example.sendgps.presentation.main.view.MainActivity
import com.example.sendgps.presentation.auth.register.RegisterContract
import com.example.sendgps.presentation.auth.register.presenter.RegisterPresenter
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

class RegisterActivity : BaseActivity(), RegisterContract.RegisterView {

    @Inject
    lateinit var presenter : RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as SendGpsApp).getAppComponent()?.injectReg(this)//different for other activities//inject
        presenter.attachView(this)


        btn_signUp.setOnClickListener {
            signUp()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_register
    }

    override fun showError(msgError: String?) {
        toast(this,msgError)
    }

    override fun showProgressBar() {
        progressBar_SignUp.visibility = View.VISIBLE
        btn_signUp.visibility = View.GONE
    }

    override fun hideProgressBar() {
        progressBar_SignUp.visibility = View.GONE
    }

    override fun signUp() {
        val fullName = etx_fullname.text.toString().trim()
        val email = etx_email_register.text.toString().trim()
        val pw1 = etx_pw1.text.toString().trim()
        val pw2 = etx_pw2.text.toString().trim()

        if (presenter.checkEmptyName(fullName)){
            etx_fullname.error = "The name is empty."
            return
        }
        if (!presenter.checkValidEmail(email)){
            etx_email_register.error = "The email is invalid."
            return
        }
        if (presenter.checkEmptyPasswords(pw1, pw2)){
            etx_pw1.error = "Empty field."
            etx_pw2.error = "Empty field."
            return
        }

        if (!presenter.checkPasswordsMatch(pw1, pw2)){
            etx_pw1.error = "Passwords don't match"
            etx_pw2.error = "Passwords don't match"
            return
        }

        presenter.signUp(fullName,email,pw1)


    }

    override fun navigateToMain() {
       val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun navigateToLogin() {
        val intent = Intent (this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
        presenter.detachJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        presenter.detachJob()
    }
}