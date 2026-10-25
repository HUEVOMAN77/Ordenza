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

package dev.spikeysanju.einsen.widget

import androidx.appwidget.appwidget.AppWidgetProvider
import androidx.glance.AppWidgetSize
import androidx.glance.BackgroundCallback
import androidx.glance.action.ActionCallback
import androidx.glance.action.InvokeActionCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateCallback
import androidx.glance.background.LinearGlancePainter
import androidx.glance.composable.background
import androidx.glance.composable.Button
import androidx.glance.composable.Column
import androidx.glance.composable.Image
import androidx.glance.composable.Text
import androidx.glance.modifiers.FillMaxSize
import androidx.glance.modifiers.Padding
import androidx.glance.modifiers.Size
import androidx.glance.modifiers.clickable
import androidx.glance.modifiers.fillMaxSize
import androidx.glance.modifiers.padding
import androidx.glance.theme.GlanceTheme
import androidx.glance.widget.CornerRadius
import androidx.glance.widget.GlanceModifier
import androidx.compose.ui.unit.dp
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.MainActivity

const val ACTION_OPEN_APP = "dev.spikeysanju.einsen.action.OPEN_APP"
const val EXTRA_WIDGET_ID = "dev.spikeysanju.einsen.extra.WIDGET_ID"

class OrdenzaAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceActionCallbacks: Map<String, ActionCallback> = mapOf(
        ACTION_OPEN_APP to OpenAppAction()
    )

    override suspend fun provideGlance(
        context: androidx.content.Context,
        id: androidx.appwidget.appwidget.AppWidgetId,
        parameters: androidx.glance.appwidget.GlanceAppWidget.Parameters?
    ): androidx.glance.appwidget.GlanceAppWidget {
        return OrdenzaAppWidget()
    }
}

class OpenAppAction : ActionCallback() {
    override suspend fun onReceive(
        context: androidx.content.Context,
        id: androidx.appwidget.appwidget.AppWidgetId,
        parameters: androidx.glance.appwidget.GlanceAppWidget.Parameters?,
        actionParameters: androidx.glance.appwidget.GlanceAppWidget.ActionParameters?
    ) {
        val intent = android.content.Intent(context, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

class OrdenzaAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: androidx.content.Context,
        id: androidx.appwidget.appwidget.AppWidgetId,
        parameters: androidx.glance.appwidget.GlanceAppWidget.Parameters?
    ): androidx.glance.appwidget.GlanceAppWidget {
        return OrdenzaAppWidget()
    }

    @androidx.glance.Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .background(
                    painter = androidx.glance.background.ColorGlancePainter(
                        androidx.glance.theme.GlanceTheme.colors.background
                    ),
                    cornerRadius = CornerRadius(dp = 12.dp)
                )
        ) {
            Image(
                imageRes = R.drawable.ic_launcher_foreground,
                contentDescription = "Ordenza",
                modifier = GlanceModifier
                    .Size(48.dp)
                    .clickable(action = androidx.glance.action.ActionCallbacks.sendAction(ACTION_OPEN_APP))
            )

            androidx.glance.composable.Spacer(modifier = GlanceModifier.Size(8.dp))

            Text(
                text = "Ordenza",
                modifier = GlanceModifier
                    .padding(top = 4.dp)
                    .clickable(action = androidx.glance.action.ActionCallbacks.sendAction(ACTION_OPEN_APP)),
                fontWeight = androidx.glance.text.FontWeight.Bold,
                fontSize = 18.sp
            )

            androidx.glance.composable.Spacer(modifier = GlanceModifier.Size(4.dp))

            Text(
                text = "Tap to open",
                fontSize = 12.sp,
                color = androidx.glance.theme.GlanceTheme.colors.secondary
            )
        }
    }
}
