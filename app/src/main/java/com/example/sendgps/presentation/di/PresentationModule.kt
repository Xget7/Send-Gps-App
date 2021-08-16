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

package com.example.sendgps.presentation.di

import com.example.sendgps.domain.interactor.loginInteractor.SignInIterator
import com.example.sendgps.domain.interactor.loginInteractor.SignInIteratorImpl
import com.example.sendgps.domain.interactor.passRecoverInteractor.PasswordRecover
import com.example.sendgps.domain.interactor.passRecoverInteractor.PasswordRecoverImpl
import com.example.sendgps.domain.interactor.registerInteractor.SignUpInteractor
import com.example.sendgps.domain.interactor.registerInteractor.SignUpInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PresentationModule {

    @Provides
    @Singleton
    fun providesSignIn(): SignInIterator = SignInIteratorImpl()

    @Provides
    @Singleton
    fun providesSignUp(): SignUpInteractor = SignUpInteractorImpl()

    @Provides
    @Singleton
    fun providesRecoverPassword(): PasswordRecover = PasswordRecoverImpl()
}