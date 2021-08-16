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

package com.example.sendgps.presentation.timer.timerlogic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.sendgps.domain.ForegroundServices.ForegroundOnlyLocationService
import com.example.sendgps.presentation.timer.view.TimerActivity
import com.example.sendgps.util.NotificationUtil
import com.example.sendgps.util.PrefUtil
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_timer.*

class TimerExpiredReceiver : BroadcastReceiver() {




    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }



}