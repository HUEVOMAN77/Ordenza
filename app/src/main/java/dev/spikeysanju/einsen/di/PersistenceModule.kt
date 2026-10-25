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

package dev.spikeysanju.einsen.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.spikeysanju.einsen.data.local.datastore.ThemeManager
import dev.spikeysanju.einsen.data.local.datastore.ThemeManagerImpl
import dev.spikeysanju.einsen.data.local.db.EinsenDatabase
import dev.spikeysanju.einsen.data.local.db.TaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `subtask` (" +
                        "`taskId` INTEGER NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`isCompleted` INTEGER NOT NULL DEFAULT 0, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "FOREIGN KEY(`taskId`) REFERENCES `task`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_subtask_taskId` ON `subtask` (`taskId`)")
        }
    }

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): ThemeManager {
        return ThemeManagerImpl(context = context)
    }

    @Singleton
    @Provides
    fun provideTaskDao(database: EinsenDatabase): TaskDao = database.getTaskDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): EinsenDatabase =
        Room.databaseBuilder(
            context,
            EinsenDatabase::class.java,
            "task-db"
        ).addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
}
