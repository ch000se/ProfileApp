package com.ch000se.profileapp.core_ui.testing

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.core.app.ComponentActivity
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [34],
    qualifiers = "w400dp-h800dp-xxhdpi",
)
abstract class AbstractComposableTest {

    @get:Rule
    val composeRule = createComposeRule()

    protected open fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit =
        content

    protected fun runTest(
        block: ComposeTestScope.() -> Unit,
    ) {
        ComposeTestScopeImpl(composeRule, ::wrapWithTheme).apply(block)
    }

    interface ComposeTestScope : SemanticsNodeInteractionsProvider {

        val context: Context

        fun setContent(
            autoAdvance: Boolean = true,
            content: @Composable () -> Unit,
        )

        fun takeScreenshot()

        fun advanceTimeBy(millis: Long)
    }

    private class ComposeTestScopeImpl(
        private val rule: ComposeContentTestRule,
        private val themeWrapper: ((@Composable () -> Unit)) -> @Composable () -> Unit,
    ) : ComposeTestScope, SemanticsNodeInteractionsProvider by rule {

        override val context: Context
            get() = (rule as AndroidComposeTestRule<*, ComponentActivity>).activity

        override fun setContent(
            autoAdvance: Boolean,
            content: @Composable (() -> Unit),
        ) {
            rule.mainClock.autoAdvance = autoAdvance
            rule.setContent(themeWrapper(content))
        }

        override fun takeScreenshot() {
            rule.onRoot().captureRoboImage()
        }

        override fun advanceTimeBy(millis: Long) {
            if (rule.mainClock.autoAdvance) {
                fail("advanceTimeBy() can be called only after setContent(autoAdvance = false) { ... }.")
            }
            rule.mainClock.advanceTimeBy(millis)
        }
    }

}
