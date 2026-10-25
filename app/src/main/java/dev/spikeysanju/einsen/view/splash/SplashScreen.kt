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

package dev.spikeysanju.einsen.view.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import dev.spikeysanju.einsen.ui.theme.typography.playfair
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.components.PulseEffect
import dev.spikeysanju.einsen.navigation.MainActions
import dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier, actions: MainActions) {

    // Animate state
    LaunchedEffect(key1 = true) {
        delay(2000L)
        actions.popBackStack.invoke()
        actions.gotoDashboard.invoke()
    }

    PulseEffect {
        // App logo with center align
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ordenza_logo),
                    contentDescription = stringResource(R.string.text_einsen_logo),
                    modifier = modifier.size(140.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = TextStyle(
                        fontFamily = playfair,
                        fontSize = 30.sp,
                        letterSpacing = 6.sp,
                        color = OrdenzaGold
                    )
                )
            }
        }
    }
}

/** Ordenza brand gold used for the wordmark. */
val OrdenzaGold = Color(0xFFAE8840)
