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

package dev.spikeysanju.einsen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.model.task.Subtask
import dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme

@Composable
fun SubtaskItem(
    modifier: Modifier = Modifier,
    subtask: Subtask,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(AppTheme.colors.card)
            .clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subtask.isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                uncheckedColor = AppTheme.colors.primary,
                checkedColor = AppTheme.colors.primary,
                checkmarkColor = AppTheme.colors.white
            )
        )

        Text(
            text = subtask.title,
            style = AppTheme.typography.body,
            textAlign = TextAlign.Start,
            color = if (subtask.isCompleted) AppTheme.colors.text.copy(alpha = 0.6f) else AppTheme.colors.text,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp)
        )

        onDelete?.let {
            IconButton(onClick = it) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.Delete,
                    contentDescription = "Delete subtask",
                    tint = AppTheme.colors.error
                )
            }
        }
    }
}
