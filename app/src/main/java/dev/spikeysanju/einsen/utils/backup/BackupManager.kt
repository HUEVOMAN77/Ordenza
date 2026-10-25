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

package dev.spikeysanju.einsen.utils.backup

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import dev.spikeysanju.einsen.data.local.db.TaskDao
import dev.spikeysanju.einsen.model.task.Subtask
import dev.spikeysanju.einsen.model.task.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TaskBackup(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val emoji: String,
    val urgency: Int,
    val importance: Int,
    val priority: String,
    val due: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val subtasks: List<SubtaskBackup> = emptyList()
)

data class SubtaskBackup(
    val id: Int,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: Long
)

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val tasks: List<TaskBackup> = emptyList()
)

class BackupManager(private val context: Context, private val taskDao: TaskDao) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    /**
     * Export all tasks and subtasks to a JSON file.
     * Returns the Uri of the exported file.
     */
    suspend fun exportToJson(): Uri? = withContext(Dispatchers.IO) {
        val tasks = taskDao.getAllTask().first()
        val taskBackups = tasks.map { task ->
            val subtasks = taskDao.getSubtasks(task.id).first()
            TaskBackup(
                id = task.id,
                title = task.title,
                description = task.description,
                category = task.category,
                emoji = task.emoji,
                urgency = task.urgency,
                importance = task.importance,
                priority = task.priority.name,
                due = task.due,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt,
                subtasks = subtasks.map { s ->
                    SubtaskBackup(
                        id = s.id,
                        taskId = s.taskId,
                        title = s.title,
                        isCompleted = s.isCompleted,
                        createdAt = s.createdAt
                    )
                }
            )
        }

        val backupData = BackupData(tasks = taskBackups)
        val jsonString = json.encodeToString(backupData)

        // Generate filename
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "ordenza_backup_${sdf.format(Date())}.json"

        // Save to external files dir (no special permissions needed)
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        file.parentFile.mkdirs()
        FileOutputStream(file).use { it.write(jsonString.toByteArray()) }

        // Generate content Uri for sharing
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    /**
     * Import tasks and subtasks from a JSON file Uri.
     * Returns the number of tasks imported.
     */
    suspend fun importFromJson(uri: Uri): Int = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext 0
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()

        val backupData: BackupData = json.decodeFromString(jsonString)

        var importedCount = 0
        for (taskBackup in backupData.tasks) {
            // Check if task already exists (by title + createdAt)
            val existing = taskDao.findTaskByID(taskBackup.id)
            if (existing != null) continue

            val task = Task(
                id = taskBackup.id,
                title = taskBackup.title,
                description = taskBackup.description,
                category = taskBackup.category,
                emoji = taskBackup.emoji,
                urgency = taskBackup.urgency,
                importance = taskBackup.importance,
                priority = dev.spikeysanju.einsen.model.task.Priority.valueOf(taskBackup.priority),
                due = taskBackup.due,
                isCompleted = taskBackup.isCompleted,
                createdAt = taskBackup.createdAt,
                updatedAt = taskBackup.updatedAt
            )

            val newId = taskDao.insertTask(task)
            importedCount++

            // Import subtasks
            for (subtaskBackup in taskBackup.subtasks) {
                val subtask = Subtask(
                    id = subtaskBackup.id,
                    taskId = newId,
                    title = subtaskBackup.title,
                    isCompleted = subtaskBackup.isCompleted,
                    createdAt = subtaskBackup.createdAt
                )
                taskDao.insertSubtask(subtask)
            }
        }

        importedCount
    }
}
