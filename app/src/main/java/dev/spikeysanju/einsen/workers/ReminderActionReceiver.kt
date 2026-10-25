/*
 *
 *  * Copyright 2021 Spikey Sanju
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package dev.spikeysanju.einsen.workers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.data.local.db.EinsenDatabase
import dev.spikeysanju.einsen.data.local.db.TaskDao
import java.util.concurrent.TimeUnit

const val ACTION_COMPLETE_TASK = "dev.spikeysanju.einsen.action.COMPLETE_TASK"
const val ACTION_SNOOZE_TASK = "dev.spikeysanju.einsen.action.SNOOZE_TASK"
const val EXTRA_TASK_ID = "dev.spikeysanju.einsen.extra.TASK_ID"
const val EXTRA_TASK_TITLE = "dev.spikeysanju.einsen.extra.TASK_TITLE"

class ReminderActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: ""

        when (action) {
            ACTION_COMPLETE_TASK -> completeTask(context, taskId, taskTitle)
            ACTION_SNOOZE_TASK -> snoozeTask(context, taskId)
        }
    }

    private fun completeTask(context: Context, taskId: Int, taskTitle: String) {
        if (taskId == -1) return

        // Mark task as completed in the database
        val dao: TaskDao = EinsenDatabase.getDatabase(context.applicationContext).getTaskDao()
        val thread = java.lang.Thread {
            dao.updateTaskStatus(taskId, true)
        }
        thread.start()

        // Cancel the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(taskId)

        // Show completion confirmation
        val builder = NotificationCompat.Builder(context, context.getString(R.string.reminder_channel_id))
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle("Task Completed")
            .setContentText("$taskTitle marked as completed")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(taskId + 10000, builder.build())
    }

    private fun snoozeTask(context: Context, taskId: Int) {
        if (taskId == -1) return

        // Cancel current notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(taskId)

        // Re-schedule reminder for 1 hour later using WorkManager
        val data = androidx.work.Data.Builder()
            .putInt(ARG_ID, taskId)
            .build()

        val worker = OneTimeWorkRequest.Builder(
            ReminderWorker::class.java
        )
            .setInitialDelay(TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("snooze_$taskId", ExistingWorkPolicy.REPLACE, worker)

        // Show snooze confirmation
        val builder = NotificationCompat.Builder(context, context.getString(R.string.reminder_channel_id))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Snoozed")
            .setContentText("Reminder postponed for 1 hour")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(taskId + 20000, builder.build())
    }
}
