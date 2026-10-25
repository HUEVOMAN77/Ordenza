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

package dev.spikeysanju.einsen.view.backup

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.navigation.MainActions
import dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme
import dev.spikeysanju.einsen.utils.backup.BackupManager
import dev.spikeysanju.einsen.view.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    actions: MainActions,
    onBackupResult: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var lastExportResult by remember { mutableStateOf<String?>(null) }
    var lastImportResult by remember { mutableStateOf<String?>(null) }

    // Activity result launcher for picking a file to import
    val pickFileLauncher: ActivityResultLauncher<Array<String>> = remember {
        ActivityResultLauncher(
            contract = OpenDocument(),
            onActivityResult = { uri ->
                if (uri != null) {
                    isImporting = true
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            val backupManager = BackupManager(context, viewModel.dao)
                            val count = backupManager.importFromJson(uri)
                            lastImportResult = "Imported $count tasks"
                            onBackupResult?.invoke(count)
                        } catch (e: Exception) {
                            lastImportResult = "Import failed: ${e.message}"
                        } finally {
                            isImporting = false
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.text_backup),
                        style = AppTheme.typography.h6,
                        color = AppTheme.colors.text
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { actions.gotoDashboard.invoke() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = AppTheme.colors.primary
                        )
                    }
                },
                backgroundColor = AppTheme.colors.background,
                elevation = 0.dp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.text_backup_title),
                style = AppTheme.typography.h1,
                color = AppTheme.colors.text,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.text_backup_description),
                style = AppTheme.typography.body,
                color = AppTheme.colors.text.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Export card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.CloudUpload,
                        contentDescription = "Export",
                        tint = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.text_export_backup),
                        style = AppTheme.typography.subtitle,
                        color = AppTheme.colors.text
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.text_export_backup_desc),
                        style = AppTheme.typography.caption,
                        color = AppTheme.colors.text.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isExporting = true
                            lifecycleOwner.lifecycleScope.launch {
                                try {
                                    val backupManager = BackupManager(context, viewModel.dao)
                                    val uri = backupManager.exportToJson()
                                    if (uri != null) {
                                        lastExportResult = "Backup exported"
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            type = "application/json"
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share backup"))
                                    } else {
                                        lastExportResult = "Export failed"
                                    }
                                } catch (e: Exception) {
                                    lastExportResult = "Export failed: ${e.message}"
                                } finally {
                                    isExporting = false
                                }
                            }
                        },
                        enabled = !isExporting
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            if (isExporting) {
                                androidx.compose.foundation.layout.CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (isExporting) "Exporting..." else stringResource(R.string.text_export_button),
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Import card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.CloudDownload,
                        contentDescription = "Import",
                        tint = androidx.compose.ui.graphics.Color(0xFF2196F3),
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.text_import_backup),
                        style = AppTheme.typography.subtitle,
                        color = AppTheme.colors.text
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.text_import_backup_desc),
                        style = AppTheme.typography.caption,
                        color = AppTheme.colors.text.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            pickFileLauncher.launch(arrayOf("application/json"))
                        },
                        enabled = !isImporting
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                        ) {
                            if (isImporting) {
                                androidx.compose.foundation.layout.CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (isImporting) "Importing..." else stringResource(R.string.text_import_button),
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results
            lastExportResult?.let { result ->
                Text(
                    text = result,
                    style = AppTheme.typography.caption,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            lastImportResult?.let { result ->
                Text(
                    text = result,
                    style = AppTheme.typography.caption,
                    color = if (result.contains("failed")) androidx.compose.ui.graphics.Color(0xFFF44336) else androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
