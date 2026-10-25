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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.spikeysanju.einsen.MainActivity
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.data.local.db.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val ARG_ID = "arg_id"

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    private val taskDao: TaskDao,
    private val notificationManager: NotificationManager
) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val reminderId = workerParams.inputData.getInt(ARG_ID, -1)
        if (reminderId == -1) {
            return Result.failure()
        }

        withContext(Dispatchers.IO) {

            // Task may have been deleted or completed since the reminder was scheduled
            val task = taskDao.findTaskByID(reminderId)
            if (task == null || task.isCompleted) return@withContext

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    context.getString(R.string.reminder_channel_id),
                    context.getString(R.string.reminder_channel_name),
                    NotificationManager.IMPORTANCE_HIGH,
                )
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                channel.enableVibration(true)
                channel.setBypassDnd(true)
                notificationManager.createNotificationChannel(channel)
            }

            val builder =
                NotificationCompat.Builder(context, context.getString(R.string.reminder_channel_id))
                    .apply {
                        setContentTitle(task.title)
                        setSmallIcon(R.drawable.ic_notification)
                        setCategory(NotificationCompat.CATEGORY_ALARM)
                        setContentText(context.getString(R.string.reminder_due_in_one_hour))
                        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        setDefaults(Notification.DEFAULT_SOUND)
                        setDefaults(Notification.DEFAULT_VIBRATE)
                    }
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setAutoCancel(true)
            builder.setContentIntent(
                PendingIntent.getActivity(
                    context,
                    task.id,
                    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            notificationManager.notify(task.id, builder.build())
        }
        return Result.success()
    }
}
