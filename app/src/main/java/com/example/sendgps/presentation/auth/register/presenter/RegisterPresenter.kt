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

package com.example.sendgps.presentation.auth.register.presenter

import androidx.core.util.PatternsCompat
import com.example.sendgps.domain.interactor.registerInteractor.SignUpInteractor
import com.example.sendgps.presentation.auth.login.exceptions.FirebaseLoginException
import com.example.sendgps.presentation.auth.register.RegisterContract
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RegisterPresenter @Inject constructor(private val signUpInteractor: SignUpInteractor) : RegisterContract.RegisterPresenter, CoroutineScope {

    var view: RegisterContract.RegisterView? = null


    private val job = Job()//job = background
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun attachView(view: RegisterContract.RegisterView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun detachJob() {
        coroutineContext.cancel()
    }

    override fun isViewAttached(): Boolean {
        return view != null
    }

    override fun checkValidEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun checkEmptyName(fullName: String): Boolean {
        return fullName.isEmpty()
    }


    override fun checkEmptyPasswords(pw1: String, pw2: String): Boolean {
        return pw1.isEmpty() or pw2.isEmpty()
    }

    override fun checkPasswordsMatch(pw1: String, pw2: String): Boolean {
        return pw1 == pw2
    }


    override fun signUp(fullName: String, email: String, pw: String) {
        launch {
            view?.showProgressBar()
            try {
                signUpInteractor.signUp(fullName,email,pw)
                if (isViewAttached()) {
                    view?.hideProgressBar()
                    view?.navigateToMain()
                }

            } catch (e: FirebaseLoginException) {
                view?.showError(e.message)
                view?.hideProgressBar()
            }
        }
    }
}