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

 package com.example.sendgps.presentation.auth.login.presenter

import com.example.sendgps.domain.interactor.loginInteractor.SignInIterator
import com.example.sendgps.presentation.auth.login.LoginContract
import com.example.sendgps.presentation.auth.login.exceptions.FirebaseLoginException
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

 class LoginPresenter @Inject constructor(private  val signInInteractor: SignInIterator) : LoginContract.LoginPresenter, CoroutineScope {

    var view:LoginContract.LoginView? = null

     private val job = Job()//job = background
     override val coroutineContext: CoroutineContext
         get() = Dispatchers.Main + job

    override fun attachView(view: LoginContract.LoginView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

     override fun detachJob() {
         coroutineContext.cancel()//cancel asynchronous task in background
     }

    override fun isViewAttached(): Boolean {
        return view != null
    }

    override fun signInUserWithEmailAndPassword(email: String, password: String) {

        //suspend functions
        launch {
            view?.showProgressBar()
            try {
                signInInteractor.signInWithEmailAndPassword(email, password)
                        if (isViewAttached()){
                            view?.hideProgressBar()
                            view?.navigateToMain()
                        }
            }catch (e:FirebaseLoginException){
                if (isViewAttached()){
                    view?.showError(e.message)
                    view?.hideProgressBar()
                }
            }
        }
    }

    override fun checkEmptyFields(email: String, password: String): Boolean {
        return email.isEmpty() || password.isEmpty()
    }
}