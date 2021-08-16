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

package com.example.sendgps.presentation.auth.passwordRecover.presenter

import com.example.sendgps.domain.interactor.passRecoverInteractor.PasswordRecover
import com.example.sendgps.presentation.auth.passwordRecover.PasswordRecoverContract
import com.example.sendgps.presentation.auth.passwordRecover.exception.PasswordRecoverException
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PasswordRecoverPresenter @Inject constructor(private val passwordRecoverInteractor:PasswordRecover): PasswordRecoverContract.PasswordRecoverPresenter ,CoroutineScope{

    var view: PasswordRecoverContract.PasswordRecoverView? = null

    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun attachView(passwordRecoverView: PasswordRecoverContract.PasswordRecoverView) {
        this.view = passwordRecoverView
    }

    override fun detachView() {
       view = null
    }

    override fun detachJob() {
        coroutineContext.cancel()
    }

    override fun isViewAttached(): Boolean {
        return view != null
    }

    override fun sendPasswordRecover(email: String) {
        launch { //suspend = launch
            try {
                view?.showProgress()
                passwordRecoverInteractor.sendPasswordResetEmail(email)
                view?.hideProgress()
                view?.navigateToLogin()

            }catch (e:PasswordRecoverException){
                view?.hideProgress()
                view?.showError(e.message)
            }
        }
    }
}