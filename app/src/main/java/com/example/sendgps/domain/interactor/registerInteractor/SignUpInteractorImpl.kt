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

package com.example.sendgps.domain.interactor.registerInteractor

import com.example.sendgps.presentation.auth.login.exceptions.FirebaseLoginException
import com.example.sendgps.presentation.auth.login.exceptions.FirebaseRegisterException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignUpInteractorImpl : SignUpInteractor {




    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ) :Unit = suspendCancellableCoroutine { continuation ->

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            val db : FirebaseFirestore = FirebaseFirestore.getInstance()
            val data = hashMapOf("userName" to fullName)
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            if (it.isSuccessful){
                db.collection("users")
                    .document(uid)
                    .set(data)
                continuation.resume(Unit)
            }else{
                continuation.resumeWithException(FirebaseRegisterException(it.exception?.message))
            }
        }

    }
}