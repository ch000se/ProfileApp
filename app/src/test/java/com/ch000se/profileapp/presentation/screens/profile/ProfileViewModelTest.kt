@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.presentation.screens.profile

import app.cash.turbine.test
import com.ch000se.profileapp.core.coroutines.TestAppDispatchers
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import com.ch000se.profileapp.domain.usecases.LogoutUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProfileViewModel")
class ProfileViewModelTest {

    @MockK
    private lateinit var getUserUseCase: GetUserUseCase

    @MockK
    private lateinit var logoutUseCase: LogoutUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var testDispatchers: TestAppDispatchers

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        testDispatchers = TestAppDispatchers(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ProfileViewModel(
        getUserUseCase = getUserUseCase,
        logoutUseCase = logoutUseCase,
        dispatchers = testDispatchers
    )

    private fun TestScope.collectState(viewModel: ProfileViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN state is loading`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)

            assertTrue(viewModel.uiState.value.isLoading)
        }
    }

    @Nested
    @DisplayName("Load User")
    inner class LoadUser {

        @Test
        fun `GIVEN user exists WHEN loading user THEN updates state with user`() = runTest {
            val expectedUser = createUser(name = "Bohdan", surname = "Petrenko")
            coEvery { getUserUseCase() } returns expectedUser

            val viewModel = createViewModel()
            collectState(viewModel)
            viewModel.onAction(ProfileUiAction.LoadUser)

            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.user)
            assertEquals("Bohdan", viewModel.uiState.value.user?.name)
        }

        @Test
        fun `GIVEN user does not exist WHEN loading user THEN state has null user`() = runTest {
            coEvery { getUserUseCase() } returns null

            val viewModel = createViewModel()
            collectState(viewModel)
            viewModel.onAction(ProfileUiAction.LoadUser)

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.user)
        }

        @Test
        fun `GIVEN use case fails WHEN loading user THEN sets error state`() = runTest {
            val expectedErrorMessage = "Failed to load user"
            coEvery { getUserUseCase() } throws Exception(expectedErrorMessage)

            val viewModel = createViewModel()
            collectState(viewModel)
            viewModel.onAction(ProfileUiAction.LoadUser)

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(expectedErrorMessage, viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("Logout Dialog")
    inner class LogoutDialog {

        @Test
        fun `GIVEN show logout dialog action WHEN triggered THEN shows dialog`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            viewModel.onAction(ProfileUiAction.ShowLogoutDialog)

            assertTrue(viewModel.uiState.value.showLogoutDialog)
        }

        @Test
        fun `GIVEN dismiss logout dialog action WHEN triggered THEN hides dialog`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            viewModel.onAction(ProfileUiAction.ShowLogoutDialog)
            viewModel.onAction(ProfileUiAction.DismissLogoutDialog)

            assertFalse(viewModel.uiState.value.showLogoutDialog)
        }
    }

    @Nested
    @DisplayName("Logout")
    inner class Logout {

        @Test
        fun `GIVEN confirm logout action WHEN triggered THEN emits NavigateToCreateProfile side effect`() =
            runTest {
                coEvery { logoutUseCase() } returns Unit

                val viewModel = createViewModel()
                viewModel.sideEffect.test {
                    viewModel.onAction(ProfileUiAction.ConfirmLogout)

                    assertEquals(ProfileUiEvent.NavigateToCreateProfile, awaitItem())
                    coVerify(exactly = 1) { logoutUseCase() }
                }
            }
    }

    private fun createUser(
        name: String = "Volodymyr",
        surname: String = "Kravchenko",
        phone: String = "+380501112233",
        email: String = "volodymyr.kravchenko@gmail.com",
        dateOfBirthday: String = "12.04.1991",
        avatar: String = ""
    ) = User(
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatar = avatar
    )
}
