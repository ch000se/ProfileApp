package com.ch000se.profileapp.presentation.screens.profile

import app.cash.turbine.test
import com.ch000se.profileapp.core_ui.testing.AbstractViewModelTest
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import com.ch000se.profileapp.domain.usecases.LogoutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ProfileViewModel")
class ProfileViewModelTest : AbstractViewModelTest<ProfileViewModel>() {

    @MockK
    private lateinit var getUserUseCase: GetUserUseCase

    @MockK
    private lateinit var logoutUseCase: LogoutUseCase

    override fun createViewModel() = ProfileViewModel(
        getUserUseCase = getUserUseCase,
        logoutUseCase = logoutUseCase
    )

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN state is loading`() = runTest {
            coEvery { getUserUseCase() } returns null

            createViewModel()

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

            createViewModel()
            viewModel.onAction(ProfileUiAction.LoadUser)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.user)
            assertEquals("Bohdan", viewModel.uiState.value.user?.name)
        }

        @Test
        fun `GIVEN user does not exist WHEN loading user THEN state has null user`() = runTest {
            coEvery { getUserUseCase() } returns null

            createViewModel()
            viewModel.onAction(ProfileUiAction.LoadUser)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.user)
        }

        @Test
        fun `GIVEN use case fails WHEN loading user THEN sets error state`() = runTest {
            val expectedErrorMessage = "Failed to load user"
            coEvery { getUserUseCase() } throws Exception(expectedErrorMessage)

            createViewModel()
            viewModel.onAction(ProfileUiAction.LoadUser)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(expectedErrorMessage, viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("Logout Dialog")
    inner class LogoutDialog {

        @Test
        fun `GIVEN show logout dialog action WHEN triggered THEN shows dialog`() = runTest {
            coEvery { getUserUseCase() } returns null

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(ProfileUiAction.ShowLogoutDialog)

            assertTrue(viewModel.uiState.value.showLogoutDialog)
        }

        @Test
        fun `GIVEN dismiss logout dialog action WHEN triggered THEN hides dialog`() = runTest {
            coEvery { getUserUseCase() } returns null

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(ProfileUiAction.ShowLogoutDialog)
            viewModel.onAction(ProfileUiAction.DismissLogoutDialog)

            assertFalse(viewModel.uiState.value.showLogoutDialog)
        }
    }

    @Nested
    @DisplayName("Logout")
    inner class Logout {

        @Test
        fun `GIVEN confirm logout action WHEN triggered THEN emits NavigateToCreateProfile side effect`() = runTest {
            coEvery { getUserUseCase() } returns null
            coEvery { logoutUseCase() } returns Unit

            createViewModel()
            advanceUntilIdle()
            viewModel.sideEffect.test {
                viewModel.onAction(ProfileUiAction.ConfirmLogout)
                advanceUntilIdle()

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
