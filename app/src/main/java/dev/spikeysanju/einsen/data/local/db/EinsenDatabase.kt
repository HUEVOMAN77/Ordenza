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

package dev.spikeysanju.einsen.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.spikeysanju.einsen.model.task.Subtask
import dev.spikeysanju.einsen.model.task.Task

@Database(entities = [Task::class, Subtask::class], version = 4, exportSchema = true)
abstract class EinsenDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: EinsenDatabase? = null

        fun getDatabase(context: Context): EinsenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EinsenDatabase::class.java,
                    "task-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
