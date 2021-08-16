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

package com.example.sendgps.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.sendgps.presentation.main.view.MainActivity
import com.example.sendgps.presentation.timer.view.TimerActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrefUtil : MainActivity() {



    companion object{//minutes

        private const val TIMER_LENGTH_ID = "com.example.sendgps.util.timer_length"
        fun getTimerLength(context: Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context )
            return preferences.getInt(TIMER_LENGTH_ID, 10)
        }
        private const val PREVIUS_TIMER_LENGTH_SECONDS_ID = "com.example.sendgps.previus_timer_length"

        fun getPreviousTimerLengthSeconds(context: Context):Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context )
            return preferences.getLong(PREVIUS_TIMER_LENGTH_SECONDS_ID, 0)
        }
        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.example.sendgps.timer_state"


        fun getTimerState(context: Context): TimerActivity.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID,0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID,ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.example.sendgps.seconds_remaining"

        fun getSecondsRemaining(context: Context):Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context )
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }
        fun setSecondsRemaining(seconds: Long,context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }
        private const val  ALARM_SET_TIME_ID = "com.example.sendgps.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }


}