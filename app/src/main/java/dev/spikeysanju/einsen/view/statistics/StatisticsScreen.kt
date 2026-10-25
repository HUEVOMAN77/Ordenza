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

package dev.spikeysanju.einsen.view.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DrawStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import dev.spikeysanju.einsen.R
import dev.spikeysanju.einsen.model.statistics.TaskDayStat
import dev.spikeysanju.einsen.model.statistics.TaskPriorityStat
import dev.spikeysanju.einsen.model.task.Priority
import dev.spikeysanju.einsen.navigation.MainActions
import dev.spikeysanju.einsen.ui.theme.apptheme.AppTheme
import dev.spikeysanju.einsen.utils.viewstate.ViewState
import dev.spikeysanju.einsen.view.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    actions: MainActions
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Priorities", "Activity")

    var completedCount by remember { mutableStateOf(0) }
    var pendingCount by remember { mutableStateOf(0) }
    var priorityStats by remember { mutableStateOf(emptyList<TaskPriorityStat>()) }
    var activityStats by remember { mutableStateOf(emptyList<TaskDayStat>()) }

    val feedState by viewModel.feed.collectAsState()

    LaunchedEffect(feedState) {
        if (feedState is ViewState.Success) {
            val tasks = feedState.task
            completedCount = tasks.count { it.isCompleted }
            pendingCount = tasks.count { !it.isCompleted }

            val stats = mutableListOf<TaskPriorityStat>()
            Priority.values().forEach { p ->
                val count = tasks.count { it.priority == p }
                stats.add(TaskPriorityStat(p.name, count))
            }
            priorityStats = stats
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.text_statistics),
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
            // Tab selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    TextButton(
                        onClick = { selectedTab = index },
                        colors = androidx.compose.material.TextButtonDefaults.textColors(
                            if (isSelected) AppTheme.colors.primary else AppTheme.colors.text.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = tab,
                            style = AppTheme.typography.subtitle.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.text.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                0 -> OverviewTab(
                    completedCount = completedCount,
                    pendingCount = pendingCount
                )
                1 -> PriorityTab(
                    stats = priorityStats
                )
                2 -> ActivityTab(
                    stats = activityStats
                )
            }
        }
    }
}

@Composable
fun OverviewTab(completedCount: Int, pendingCount: Int) {
    val total = completedCount + pendingCount
    val completionPercent = if (total == 0) 0f else (completedCount * 100 / total).toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Task Completion",
            style = AppTheme.typography.h1,
            color = AppTheme.colors.text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clipToBounds(),
            colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.card),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center
            ) {
                Canvas(
                    modifier = Modifier.size(140.dp).align(Alignment.CenterHorizontally)
                ) {
                    val center = size.center
                    val radius = min(size.width, size.height) / 2
                    val sweepAngle = completionPercent * 3.6f

                    drawArc(
                        color = AppTheme.colors.surface.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = center - radius,
                        bottomRight = center + radius,
                        style = DrawStyle(StrokeCap.Round, 16.dp.toPx())
                    )

                    drawArc(
                        color = AppTheme.colors.primary,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = center - radius,
                        bottomRight = center + radius,
                        style = DrawStyle(StrokeCap.Round, 16.dp.toPx())
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$completionPercent%",
                    style = AppTheme.typography.h1.copy(fontSize = 32.sp),
                    color = AppTheme.colors.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "of $total tasks completed",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.text.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Completed", value = completedCount.toString(), color = Color(0xFF4CAF50))
            StatCard(title = "Pending", value = pendingCount.toString(), color = Color(0xFFFF9800))
            StatCard(title = "Total", value = total.toString(), color = AppTheme.colors.primary)
        }
    }
}

@Composable
fun PriorityTab(stats: List<TaskPriorityStat>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tasks by Priority",
            style = AppTheme.typography.h1,
            color = AppTheme.colors.text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val maxCount = stats.maxOfOrNull { it.count } ?: 1

        stats.forEach { stat ->
            val color = when (stat.priority) {
                Priority.URGENT.name -> Color(0xFFF44336)
                Priority.IMPORTANT.name -> Color(0xFFFF9800)
                Priority.DELEGATE.name -> Color(0xFF2196F3)
                Priority.DUMP.name -> Color(0xFF9C27B0)
                else -> AppTheme.colors.primary
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stat.priority,
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.text,
                    modifier = Modifier.width(80.dp)
                )

                Card(
                    modifier = Modifier.weight(1f).height(24.dp),
                    colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    elevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier
                            .width((stat.count.toFloat() / maxCount) * 200.dp)
                            .fillMaxHeight()
                            .background(color)
                    )

                    Text(
                        text = stat.count.toString(),
                        style = AppTheme.typography.caption,
                        color = AppTheme.colors.text.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                    )
                }
            }
        }

        if (stats.all { it.count == 0 }) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "No tasks yet",
                style = AppTheme.typography.body,
                color = AppTheme.colors.text.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ActivityTab(stats: List<TaskDayStat>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Activity (Last 7 Days)",
            style = AppTheme.typography.h1,
            color = AppTheme.colors.text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val maxCount = stats.maxOfOrNull { it.taskCount } ?: 1

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.card),
            elevation = 4.dp
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize(),
                onDraw = {
                    val width = size.width
                    val height = size.height
                    val padding = 40f
                    val chartWidth = width - padding * 2
                    val chartHeight = height - padding * 2

                    val pointCount = stats.size
                    val stepX = chartWidth / max(1, pointCount - 1)

                    // Grid lines
                    for (i in 1..4) {
                        val y = padding + (chartHeight / 4f * i)
                        drawLine(
                            color = AppTheme.colors.surface.copy(alpha = 0.1f),
                            start = Offset(padding, y),
                            end = Offset(width - padding, y),
                            strokeWidth = 1f
                        )
                    }

                    // Points
                    val points = stats.mapIndexed { index, stat ->
                        val x = padding + (index * stepX)
                        val y = padding + chartHeight - ((stat.taskCount.toFloat() / maxCount) * chartHeight)
                        Offset(x, y)
                    }

                    // Area
                    if (points.size > 1) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(points.first().x, padding + chartHeight)
                            points.forEach { lineTo(it.x, it.y) }
                            lineTo(points.last().x, padding + chartHeight)
                            close()
                        }
                        drawPath(path = path, color = AppTheme.colors.primary.copy(alpha = 0.15f))
                    }

                    // Line
                    if (points.size > 1) {
                        for (i in 1 until points.size) {
                            drawLine(
                                color = AppTheme.colors.primary,
                                start = points[i - 1],
                                end = points[i],
                                strokeWidth = 2f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Points
                    points.forEach { point ->
                        drawCircle(color = AppTheme.colors.primary, radius = 4f, center = point)
                    }

                    // Labels
                    stats.forEachIndexed { index, stat ->
                        val x = padding + (index * stepX)
                        drawText(
                            text = getDayLabel(stat.timestamp),
                            topLeft = Offset(x - 12f, padding + chartHeight + 4f),
                            fontSize = 10.sp,
                            color = AppTheme.colors.text.copy(alpha = 0.6f),
                            style = TextStyle(fontFamily = androidx.compose.ui.text.FontFamily.Default)
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (stats.any { it.taskCount > 0 }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stats.forEach { stat ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(20.dp).background(AppTheme.colors.primary).clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stat.taskCount.toString(),
                                style = AppTheme.typography.caption,
                                color = Color.White
                            )
                        }
                        Text(
                            text = getDayLabel(stat.timestamp),
                            style = AppTheme.typography.caption,
                            color = AppTheme.colors.text.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "No activity yet",
                style = AppTheme.typography.body,
                color = AppTheme.colors.text.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun getDayLabel(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    return when (day) {
        java.util.Calendar.SUNDAY -> "Sun"
        java.util.Calendar.MONDAY -> "Mon"
        java.util.Calendar.TUESDAY -> "Tue"
        java.util.Calendar.WEDNESDAY -> "Wed"
        java.util.Calendar.THURSDAY -> "Thu"
        java.util.Calendar.FRIDAY -> "Fri"
        java.util.Calendar.SATURDAY -> "Sat"
        else -> "?"
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.weight(1f).height(80.dp),
        colors = androidx.compose.material.CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = AppTheme.typography.h1.copy(fontSize = 20.sp),
                color = color,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.text.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
