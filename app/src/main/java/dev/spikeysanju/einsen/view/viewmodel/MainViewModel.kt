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

package dev.spikeysanju.einsen.view.viewmodel

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.spikeysanju.einsen.analytics.AnalyticsParams
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.spikeysanju.einsen.data.local.db.TaskDao
import dev.spikeysanju.einsen.model.emoji.EmojiItem
import dev.spikeysanju.einsen.model.task.Subtask
import dev.spikeysanju.einsen.model.task.Task
import dev.spikeysanju.einsen.repository.MainRepository
import dev.spikeysanju.einsen.utils.viewstate.CountViewState
import dev.spikeysanju.einsen.utils.viewstate.EmojiViewState
import dev.spikeysanju.einsen.utils.viewstate.SingleViewState
import dev.spikeysanju.einsen.utils.viewstate.SubtaskViewState
import dev.spikeysanju.einsen.utils.viewstate.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: MainRepository,
    private val taskDao: TaskDao
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    private val _singleViewState = MutableStateFlow<SingleViewState>(SingleViewState.Loading)
    private val _emojiViewState = MutableStateFlow<EmojiViewState>(EmojiViewState.Loading)
    private val _countState = MutableStateFlow<CountViewState>(CountViewState.Loading)
    private val _currentEmoji = MutableStateFlow("")
    private val _subtaskViewState = MutableStateFlow<SubtaskViewState>(SubtaskViewState.Loading)

    // The UI collects from this StateFlow to get its state update
    val feed = _viewState.asStateFlow()
    val singleTask = _singleViewState.asStateFlow()
    val emoji = _emojiViewState.asStateFlow()
    val currentEmoji = _currentEmoji.asStateFlow()
    val subtasks = _subtaskViewState.asStateFlow()

    private var cachedEmoji: List<EmojiItem>? = null
    private var feedJob: Job? = null
    private var singleTaskJob: Job? = null
    private var subtaskJob: Job? = null

    val dao: TaskDao
        get() = taskDao

    // get all task
    fun getAllTask() {
        feedJob?.cancel()
        feedJob = launchGetAllTask()
    }

    private fun launchGetAllTask() = viewModelScope.launch(Dispatchers.IO) {
        repo.getAllTask().distinctUntilChanged().collect { result ->
            try {
                if (result.isNullOrEmpty()) {
                    _viewState.value = ViewState.Empty
                } else {
                    _viewState.value = ViewState.Success(result)
                }
            } catch (e: Exception) {
                _viewState.value = ViewState.Error(e)
            }
        }
    }

    // get all list of emoji from Json
    fun getAllEmoji(context: Context, searchQuery: String) = viewModelScope.launch {
        try {
            // read & decode JSON only once, then reuse the cached list
            val decodedEmoji = cachedEmoji ?: withContext(Dispatchers.IO) {
                val myJson = context.assets.open("emoji.json").bufferedReader().use { it.readText() }
                val format = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                format.decodeFromString<List<EmojiItem>>(myJson).distinct()
            }.also { cachedEmoji = it }

            // filter the emoji based on Aliases
            val filteredEmojiAliases = decodedEmoji.filter { emojiAliases ->
                emojiAliases.aliases.any {
                    it.contains(searchQuery, ignoreCase = true)
                } || emojiAliases.category.contains(searchQuery, ignoreCase = true)
            }.distinct()

            if (searchQuery.isNullOrEmpty()) {
                _emojiViewState.value = EmojiViewState.Success(decodedEmoji)
            } else {
                if (filteredEmojiAliases.isNullOrEmpty()) {
                    _emojiViewState.value = EmojiViewState.Empty
                } else {
                    _emojiViewState.value = EmojiViewState.Success(filteredEmojiAliases)
                }
            }
        } catch (e: Exception) {
            _emojiViewState.value = EmojiViewState.Error(exception = e)
        }
    }

    // insert source
    fun insertTask(task: Task, onSaved: (Task) -> Unit = {}) = viewModelScope.launch {
        val rowId = repo.insert(task)
        // Room returns the generated row id; use it so reminders point to the real task
        val saved = if (task.id == 0 && rowId > 0) task.copy(id = rowId.toInt()) else task
        onSaved(saved)
    }

    // delete source
    fun deleteTaskByID(id: Int) = viewModelScope.launch {
        repo.delete(id)
    }

    // update status
    fun updateStatus(id: Int, isCompleted: Boolean) = viewModelScope.launch {
        repo.updateStatus(id, isCompleted)
    }

    // find task by id
    fun findTaskByID(id: Int) {
        singleTaskJob?.cancel()
        singleTaskJob = launchFindTaskByID(id)
    }

    private fun launchFindTaskByID(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repo.find(id).distinctUntilChanged().collect { result ->
            try {
                if (result == null || result.title.isEmpty()) {
                    _singleViewState.value = SingleViewState.Empty
                } else {
                    _singleViewState.value = SingleViewState.Success(result)
                }
            } catch (e: Exception) {
                _viewState.value = ViewState.Error(e)
            }
        }
    }

    // get tasks by priority
    fun getTaskByPriority(priority: String) {
        feedJob?.cancel()
        feedJob = launchGetTaskByPriority(priority)
    }

    private fun launchGetTaskByPriority(priority: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.getTaskByPriority(priority).distinctUntilChanged().collect { result ->
            try {
                if (result.isNullOrEmpty()) {
                    _viewState.value = ViewState.Empty
                } else {
                    _viewState.value = ViewState.Success(result)
                }
            } catch (e: Exception) {
                _viewState.value = ViewState.Error(e)
            }
        }
    }

    // update status
    fun currentEmoji(emoji: String) = viewModelScope.launch {
        try {
            if (emoji.isEmpty()) {
                _currentEmoji.value = "Select an emoji"
            } else {
                _currentEmoji.value = emoji
            }
        } catch (e: Exception) {
            _currentEmoji.value = "⚠️"
        }
    }

    // get subtasks for a task
    fun getSubtasks(taskId: Int) {
        subtaskJob?.cancel()
        subtaskJob = launchGetSubtasks(taskId)
    }

    private fun launchGetSubtasks(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repo.getSubtasks(taskId).distinctUntilChanged().collect { result ->
            try {
                if (result.isNullOrEmpty()) {
                    _subtaskViewState.value = SubtaskViewState.Empty
                } else {
                    _subtaskViewState.value = SubtaskViewState.Success(result)
                }
            } catch (e: Exception) {
                _subtaskViewState.value = SubtaskViewState.Error(e)
            }
        }
    }

    // insert subtask
    suspend fun insertSubtask(subtask: Subtask): Long {
        val id = repo.insertSubtask(subtask)
        return id
    }

    // update subtask
    suspend fun updateSubtask(subtask: Subtask) {
        repo.updateSubtask(subtask)
    }

    // delete subtask
    suspend fun deleteSubtask(id: Int) {
        repo.deleteSubtask(id)
    }

    // update subtask status
    suspend fun updateSubtaskStatus(id: Int, isCompleted: Boolean) {
        repo.updateSubtaskStatus(id = id, isCompleted = isCompleted)
    }

    // Statistics: completed vs pending count
    suspend fun getCompletedCount(): Int {
        val tasks = repo.getAllTask().first()
        return tasks.count { it.isCompleted }
    }

    // Statistics: pending count
    suspend fun getPendingCount(): Int {
        val tasks = repo.getAllTask().first()
        return tasks.count { !it.isCompleted }
    }

    // Statistics: tasks created per day (last 7 days)
    suspend fun getTasksCreatedPerDay(days: Int = 7): List<TaskDayStat> {
        val tasks = repo.getAllTask().first()
        val now = System.currentTimeMillis()
        val stats = mutableListOf<TaskDayStat>()
        for (i in 0 until days) {
            val dayStart = now - (i * 86400000L) - (now % 86400000L)
            val dayEnd = dayStart + 86400000L
            val count = tasks.count { t ->
                t.createdAt >= dayStart && t.createdAt < dayEnd
            }
            stats.add(TaskDayStat(dayStart, count))
        }
        return stats.reversed()
    }

    /**
     * Local analytics: logs UI events to Logcat (debug builds only). No data leaves the device.
     */
    fun firebaseLogEvent(event: String, bundle: Bundle) {
        logcat("Analytics") { "$event ${bundle.keySet().associateWith { bundle.get(it) }}" }
    }
}
