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

package dev.spikeysanju.einsen.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.spikeysanju.einsen.model.task.Priority
import dev.spikeysanju.einsen.utils.getUrgencyImportanceFromPriority
import dev.spikeysanju.einsen.view.about.AboutScreen
import dev.spikeysanju.einsen.view.add.AddTaskScreen
import dev.spikeysanju.einsen.view.allemoji.AllEmojiScreen
import dev.spikeysanju.einsen.view.dashboard.DashboardScreen
import dev.spikeysanju.einsen.view.details.TaskDetailsScreen
import dev.spikeysanju.einsen.view.edit.EditTaskScreen
import dev.spikeysanju.einsen.view.project.ProjectScreen
import dev.spikeysanju.einsen.view.splash.SplashScreen
import dev.spikeysanju.einsen.view.task.AllTaskScreen
import dev.spikeysanju.einsen.view.viewmodel.MainViewModel
import dev.spikeysanju.einsen.view.webview.WebViewScreen

/**
 * Single source for Navigation Routes of this app.
 */
object EndPoints {
    const val ID = "id"
    const val PRIORITY = "priority"
    const val URL = "url"
    const val TITLE = "title"
    const val EMOJI = "emoji"
}

object QueryParams {
    const val URGENCY = "urgency"
    const val IMPORTANCE = "importance"
}

object EinsenModifier {
    val modifier: Modifier = Modifier
}

@Composable
fun NavGraph(toggleTheme: () -> Unit) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val actions = remember(navController) { MainActions(navController) }

    ModalBottomSheetLayout(bottomSheetNavigator) {

        NavHost(
            navController,
            startDestination = Screen.Dashboard.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) + fadeIn(tween(300))
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) + fadeOut(tween(300))
            }
        ) {

            /**
             * Navigates to [SplashScreen].
             */
            composable(Screen.Splash.route) {
                SplashScreen(EinsenModifier.modifier, actions)
            }

            /**
             * Navigates to [Project].
             */
            composable(
                Screen.Project.route
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                ProjectScreen(EinsenModifier.modifier, viewModel, actions, toggleTheme)
            }

            /**
             * Navigates to [Dashboard].
             */
            composable(
                Screen.Dashboard.route
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                LaunchedEffect(Unit) { viewModel.getAllTask() }
                DashboardScreen(
                    EinsenModifier.modifier,
                    viewModel,
                    actions,
                    toggleTheme
                )
            }

            /**
             * Navigates to [AddTask].
             */
            composable(
                "${Screen.AddTask.route}?urgency={urgency}&importance={importance}",
                arguments = listOf(
                    navArgument(QueryParams.URGENCY) {
                        type = NavType.IntType
                        defaultValue = 0
                    },
                    navArgument(QueryParams.IMPORTANCE) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                LaunchedEffect(it) {
                    it.savedStateHandle.getStateFlow<String?>(EndPoints.EMOJI, null)
                        .collect { result -> if (result != null) viewModel.currentEmoji(result) }
                }

                val defaultUrgency = it.arguments?.getInt(QueryParams.URGENCY) ?: 0
                val defaultImportance = it.arguments?.getInt(QueryParams.IMPORTANCE) ?: 0

                AddTaskScreen(
                    EinsenModifier.modifier,
                    viewModel,
                    actions,
                    defaultUrgency,
                    defaultImportance
                )
            }

            /**
             * Navigates to [AllTask].
             * @param priority
             */
            composable(
                "${Screen.AllTask.route}/{priority}",
                arguments = listOf(navArgument(EndPoints.PRIORITY) { type = NavType.StringType })
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)

                val priority = it.arguments?.getString(EndPoints.PRIORITY)
                    ?: throw IllegalStateException("'Priority' shouldn't be null")

                LaunchedEffect(priority) { viewModel.getTaskByPriority(priority = priority) }

                val defaultUrgencyImportance = getUrgencyImportanceFromPriority(priority)

                AllTaskScreen(
                    EinsenModifier.modifier,
                    viewModel,
                    actions,
                    defaultUrgencyImportance.first,
                    defaultUrgencyImportance.second
                )
            }

            /**
             * Navigates to [TaskDetails].
             * @param [id]
             */
            composable(
                "${Screen.TaskDetails.route}/{id}",
                arguments = listOf(navArgument(EndPoints.ID) { type = NavType.IntType })
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                val taskID = it.arguments?.getInt(EndPoints.ID)
                    ?: throw IllegalStateException("'Task ID' shouldn't be null")

                LaunchedEffect(taskID) { viewModel.findTaskByID(taskID) }
                TaskDetailsScreen(EinsenModifier.modifier, viewModel, actions)
            }

            /**
             * Navigates to [EditTask].
             * @param id
             */
            composable(
                "${Screen.EditTask.route}/{id}",
                arguments = listOf(navArgument(EndPoints.ID) { type = NavType.IntType })
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                val taskID = it.arguments?.getInt(EndPoints.ID)
                    ?: throw IllegalStateException("'Task ID' shouldn't be null")

                LaunchedEffect(it) {
                    it.savedStateHandle.getStateFlow<String?>(EndPoints.EMOJI, null)
                        .collect { result -> if (result != null) viewModel.currentEmoji(result) }
                }
                LaunchedEffect(taskID) { viewModel.findTaskByID(taskID) }
                EditTaskScreen(EinsenModifier.modifier, viewModel, actions)
            }

            /**
             * Navigates to [About].
             */
            composable(
                Screen.About.route
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                AboutScreen(EinsenModifier.modifier, viewModel, actions)
            }

            /**
             * Navigates to [WebView].
             * @param title
             * @param url
             */
            composable(
                "${Screen.WebView.route}/{title}/{url}",
                arguments = listOf(
                    navArgument(EndPoints.TITLE) { type = NavType.StringType },
                    navArgument(EndPoints.URL) { type = NavType.StringType }
                )
            ) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                val url = it.arguments?.getString(EndPoints.URL)
                    ?: throw IllegalStateException("'URL' shouldn't be null")
                val title = it.arguments?.getString(EndPoints.TITLE)
                    ?: throw java.lang.IllegalStateException("'Title' shouldn't be null")
                WebViewScreen(
                    modifier = EinsenModifier.modifier,
                    title = title,
                    url = url,
                    actions = actions,
                    viewModel
                )
            }

            /**
             * Navigates to [AllEmoji].
             * @param viewModel
             * @param actions
             * @param onSelect
             */
            bottomSheet(route = Screen.AllEmoji.route) {
                val viewModel = hiltViewModel<MainViewModel>(it)
                AllEmojiScreen(
                    EinsenModifier.modifier,
                    viewModel,
                    actions,
                    onSelect = { selectedEmoji ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            EndPoints.EMOJI,
                            selectedEmoji
                        ).apply {
                            actions.popBackStack.invoke()
                        }
                    }
                )
            }
        }
    }
}

/**
 * A class to define Navigation Route to All Flows of this app with the help of [NavController]
 * @param navController
 */
class MainActions(navController: NavController) {

    val upPress: () -> Unit = {
        navController.navigateUp()
    }

    val popBackStack: () -> Unit = {
        navController.popBackStack()
    }

    val gotoDashboard: () -> Unit = {
        navController.navigate(Screen.Dashboard.route)
    }

    val gotoAddTask: (urgency: Int?, importance: Int?) -> Unit = { urgency, importance ->
        navController.navigate("${Screen.AddTask.route}?urgency=$urgency&importance=$importance")
    }

    val gotoAllTask: (priority: Priority) -> Unit = { priority ->
        navController.navigate("${Screen.AllTask.route}/${priority.name}")
    }

    val gotoTaskDetails: (id: Int) -> Unit = { id ->
        navController.navigate("${Screen.TaskDetails.route}/$id")
    }

    val gotoEditTask: (id: Int) -> Unit = { id ->
        navController.navigate("${Screen.EditTask.route}/$id")
    }

    val gotoAbout: () -> Unit = {
        navController.navigate(Screen.About.route)
    }

    val gotoWebView: (title: String, url: String) -> Unit = { title, url ->
        navController.navigate("${Screen.WebView.route}/$title/$url")
    }

    val gotoAllEmoji: () -> Unit = {
        navController.navigate(Screen.AllEmoji.route)
    }
}
