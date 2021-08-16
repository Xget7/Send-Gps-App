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

package com.example.sendgps.presentation.auth.register

interface RegisterContract {

    interface RegisterView{
        fun showError(msgError: String?)
        fun showProgressBar()
        fun hideProgressBar()
        fun signUp()
        fun navigateToMain()
        fun navigateToLogin()
    }


    interface RegisterPresenter{
        fun attachView(view : RegisterView)
        fun detachView()
        fun detachJob()
        fun isViewAttached(): Boolean
        fun checkEmptyName(fullName: String): Boolean
        fun checkValidEmail(email: String) : Boolean
        fun checkEmptyPasswords(pw1 : String , pw2 : String) :Boolean
        fun checkPasswordsMatch(pw1 : String , pw2 : String) :Boolean
        fun signUp(fullName: String, email: String, pw: String)
    }
}