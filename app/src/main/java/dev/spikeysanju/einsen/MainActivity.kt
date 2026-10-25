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

package dev.spikeysanju.einsen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dev.spikeysanju.einsen.data.local.datastore.ThemeManager
import dev.spikeysanju.einsen.navigation.NavGraph
import dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme.AppTheme
import dev.spikeysanju.einsen.ui.theme.color.darkColors
import dev.spikeysanju.einsen.ui.theme.color.lightColors
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var themeManager: ThemeManager

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EinsenMain()
        }

        /**
         * Observe the Theme Mode
         */
        observeThemeMode()

        /**
         * Android 13+ requires runtime permission to show task reminders.
         */
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    fun EinsenMain() {

        /**
         * Check if the UI Mode is in Dark Mode
         */
        val darkMode by themeManager.uiModeFlow.collectAsState(initial = isSystemInDarkTheme())

        /**
         * Change UI Mode on toggle
         */
        val toggleTheme: () -> Unit = {
            lifecycleScope.launch {
                themeManager.setDarkMode(!darkMode)
            }
        }

        val colors = if (darkMode) darkColors() else lightColors()

        AppTheme(colors = colors) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme.colors.background
            ) {
                SetStatusBarColor(darkMode)
                // Keep content clear of status/navigation bars and the keyboard (Android 15 edge-to-edge)
                Box(modifier = Modifier.safeDrawingPadding()) {
                    NavGraph(toggleTheme)
                }
            }
        }
    }

    private fun observeThemeMode() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeManager.uiModeFlow.collect {
                    val mode = when (it) {
                        true -> AppCompatDelegate.MODE_NIGHT_YES
                        false -> AppCompatDelegate.MODE_NIGHT_NO
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }
    }
}

@Composable
fun SetStatusBarColor(darkMode: Boolean) {
    val systemUiController = rememberSystemUiController()

    /**
     *  Transparent system bars (content draws behind them), with icon colors
     *  matching the app theme rather than the system theme.
     */
    SideEffect {
        systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !darkMode)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
    }
}
