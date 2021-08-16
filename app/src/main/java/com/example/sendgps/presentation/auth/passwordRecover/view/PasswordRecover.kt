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

package com.example.sendgps.presentation.auth.passwordRecover.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.sendgps.R
import com.example.sendgps.base.BaseActivity
import com.example.sendgps.presentation.auth.login.view.LoginActivity
import com.example.sendgps.presentation.auth.passwordRecover.PasswordRecoverContract
import com.example.sendgps.presentation.auth.passwordRecover.presenter.PasswordRecoverPresenter
import com.example.sendgps.SendGpsApp
import kotlinx.android.synthetic.main.activity_password_recover.*
import javax.inject.Inject

class PasswordRecover : BaseActivity() , PasswordRecoverContract.PasswordRecoverView{

    @Inject
     lateinit var presenter : PasswordRecoverPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as SendGpsApp).getAppComponent()?.injectRecover(this)//different for other activities//inject
        presenter.attachView(this)

        btn_reocover_pass.setOnClickListener {
            recoverPassword()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_password_recover
    }

    override fun showError(msgError: String?) {
        toast(this,msgError)
    }

    override fun showProgress() {
        progressBar_recover_pw.visibility = View.VISIBLE
        btn_reocover_pass.visibility = View.GONE
    }

    override fun hideProgress() {
        progressBar_recover_pw.visibility = View.GONE
        btn_reocover_pass.visibility = View.VISIBLE
    }

    override fun recoverPassword() {
        val email = etx_recover_pass.text.trim().toString()
        if (!email.isEmpty()) presenter.sendPasswordRecover(email)
        else toast(this, "Ingrese un email")
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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