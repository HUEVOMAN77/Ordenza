package dev.spikeysanju.einsen

import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end smoke test: opens the app, creates a task and verifies the
 * dashboard comes back without crashing. Runs on the GitHub Actions emulator.
 */
@RunWith(AndroidJUnit4::class)
class EinsenSmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun str(id: Int) = rule.activity.getString(id)

    @Test
    fun createTask_returnsToDashboard() {
        val dashboard = str(R.string.text_my_dashboard)
        rule.waitUntil(15_000) { rule.onAllNodesWithText(dashboard).fetchSemanticsNodes().isNotEmpty() }

        // open "Add Task" from the floating action button
        rule.onAllNodesWithContentDescription(str(R.string.text_addTask)).onFirst().performClick()
        rule.waitUntil(10_000) {
            rule.onAllNodesWithText(str(R.string.text_save_task)).fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithText(str(R.string.text_title)).fetchSemanticsNodes().isNotEmpty()
        }

        fun type(label: Int, value: String) {
            rule.onNode(hasSetTextAction() and hasText(str(label))).performTextInput(value)
        }
        type(R.string.text_title, "Test task")
        type(R.string.text_description, "Created by CI")
        type(R.string.text_category, "QA")

        val save = str(R.string.text_save_task)
        rule.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(save))
        rule.onNodeWithText(save).performClick()

        // back on the dashboard, and the empty state is gone
        rule.waitUntil(10_000) { rule.onAllNodesWithText(dashboard).fetchSemanticsNodes().isNotEmpty() }
        rule.waitUntil(10_000) {
            rule.onAllNodesWithText(str(R.string.text_no_task_title)).fetchSemanticsNodes().isEmpty()
        }
    }
}
