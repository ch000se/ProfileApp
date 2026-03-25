@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.core_ui.testing

import androidx.lifecycle.ViewModel
import com.ch000se.profileapp.core.mvi.MVI
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractViewModelTest<VM> where VM : ViewModel, VM : MVI<*, *, *> {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    abstract fun createViewModel(): VM

    protected fun runTest(block: suspend ViewModelTestScope<VM>.() -> Unit) {
        testScope.runTest {
            ViewModelTestScopeImpl(this, ::createViewModel).apply { block() }
        }
    }

    interface ViewModelTestScope<VM> where VM : ViewModel, VM : MVI<*, *, *> {

        val viewModel: VM

        fun createViewModel()

        fun advanceTimeBy(delayTimeMillis: Long)

        fun advanceUntilIdle()
    }

    private class ViewModelTestScopeImpl<VM>(
        private val scope: TestScope,
        private val factory: () -> VM
    ) : ViewModelTestScope<VM> where VM : ViewModel, VM : MVI<*, *, *> {

        private var _viewModel: VM? = null

        override val viewModel: VM
            get() = _viewModel ?: error("Call createViewModel() first")

        override fun createViewModel() {
            _viewModel = factory()
            scope.backgroundScope.launch {
                viewModel.uiState.collect()
            }
            scope.advanceUntilIdle()
            scope.runCurrent()
            scope.advanceUntilIdle()
        }

        override fun advanceTimeBy(delayTimeMillis: Long) {
            scope.advanceTimeBy(delayTimeMillis)
        }

        override fun advanceUntilIdle() {
            scope.advanceUntilIdle()
        }
    }
}
