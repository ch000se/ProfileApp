@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.presentation

import com.ch000se.profileapp.domain.usecases.IsUserExistUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MainViewModel")
class MainViewModelTest {

    @MockK
    private lateinit var isUserExistUseCase: IsUserExistUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MainViewModel {
        return MainViewModel(isUserExistUseCase)
    }

    @Nested
    @DisplayName("User Existence Check")
    inner class UserExistenceCheck {

        @Test
        fun `GIVEN user exists WHEN ViewModel initialized THEN state is Ready with isUserExist true`() =
            runTest {
                coEvery { isUserExistUseCase() } returns true

                val viewModel = createViewModel()
                testDispatcher.scheduler.advanceUntilIdle()

                val state = viewModel.uiState.value as MainUiState.Ready
                assertTrue(state.isUserExist)
                coVerify(exactly = 1) { isUserExistUseCase() }
            }

        @Test
        fun `GIVEN user does not exist WHEN ViewModel initialized THEN state is Ready with isUserExist false`() =
            runTest {
                coEvery { isUserExistUseCase() } returns false

                val viewModel = createViewModel()
                testDispatcher.scheduler.advanceUntilIdle()

                val state = viewModel.uiState.value as MainUiState.Ready
                assertEquals(false, state.isUserExist)
                coVerify(exactly = 1) { isUserExistUseCase() }
            }

        @Test
        fun `GIVEN use case called WHEN ViewModel initialized THEN state transitions to Ready`() =
            runTest {
                coEvery { isUserExistUseCase() } returns true

                val viewModel = createViewModel()
                testDispatcher.scheduler.advanceUntilIdle()

                assertTrue(viewModel.uiState.value is MainUiState.Ready)
            }
    }
}
