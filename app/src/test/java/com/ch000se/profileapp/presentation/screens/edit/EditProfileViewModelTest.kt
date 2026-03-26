@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.presentation.screens.edit

import app.cash.turbine.test
import com.ch000se.profileapp.core.coroutines.TestAppDispatchers
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import com.ch000se.profileapp.domain.usecases.SaveUserUseCase
import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.UserValidator
import com.ch000se.profileapp.domain.validation.ValidationError
import com.ch000se.profileapp.domain.validation.ValidationResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("EditProfileViewModel")
class EditProfileViewModelTest {

    @MockK
    private lateinit var getUserUseCase: GetUserUseCase

    @MockK
    private lateinit var saveUserUseCase: SaveUserUseCase

    @MockK
    private lateinit var userValidator: UserValidator

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

    private fun setupValidatorMocks() {
        every { userValidator.validateName(any()) } returns null
        every { userValidator.validateSurname(any()) } returns null
        every { userValidator.validatePhone(any()) } returns null
        every { userValidator.validateEmail(any()) } returns null
        every { userValidator.validateDateOfBirthday(any()) } returns null
    }

    private fun createViewModel(): EditProfileViewModel {
        coEvery { getUserUseCase() } returns null
        setupValidatorMocks()
        return EditProfileViewModel(
            getUserUseCase = getUserUseCase,
            saveUserUseCase = saveUserUseCase,
            userValidator = userValidator,
            dispatchers = testDispatchers
        )
    }

    private fun TestScope.collectState(viewModel: EditProfileViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN user exists WHEN loading THEN populates state with user data`() = runTest {
            val expectedUser = createUser(name = "Kateryna", surname = "Savchenko")
            coEvery { getUserUseCase() } returns expectedUser
            setupValidatorMocks()

            val viewModel = EditProfileViewModel(
                getUserUseCase = getUserUseCase,
                saveUserUseCase = saveUserUseCase,
                userValidator = userValidator,
                dispatchers = testDispatchers
            )
            collectState(viewModel)
            advanceUntilIdle()

            assertEquals("Kateryna", viewModel.uiState.value.name)
            assertEquals("Savchenko", viewModel.uiState.value.surname)
        }

        @Test
        fun `GIVEN no user exists WHEN loading THEN state remains empty`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            assertEquals("", viewModel.uiState.value.name)
            assertEquals("", viewModel.uiState.value.surname)
        }
    }

    @Nested
    @DisplayName("Field Validation")
    inner class FieldValidation {

        @Test
        fun `GIVEN valid name WHEN updating name THEN updates state without error`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.onAction(EditProfileUiAction.UpdateName("Mykola"))

            assertEquals("Mykola", viewModel.uiState.value.name)
            assertFalse(viewModel.uiState.value.validationErrors.containsKey(UserField.NAME))
        }

        @Test
        fun `GIVEN invalid name WHEN updating name THEN sets validation error`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            every { userValidator.validateName("") } returns ValidationError.NameRequired
            viewModel.onAction(EditProfileUiAction.UpdateName(""))

            assertEquals(
                ValidationError.NameRequired,
                viewModel.uiState.value.validationErrors[UserField.NAME]
            )
            assertFalse(viewModel.uiState.value.isSaveButtonEnabled)
        }

        @Test
        fun `GIVEN invalid email WHEN updating email THEN sets validation error`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            every { userValidator.validateEmail("invalid-email") } returns ValidationError.EmailInvalid
            viewModel.onAction(EditProfileUiAction.UpdateEmail("invalid-email"))

            assertEquals(
                ValidationError.EmailInvalid,
                viewModel.uiState.value.validationErrors[UserField.EMAIL]
            )
        }

        @Test
        fun `GIVEN all fields valid WHEN updating fields THEN enables save button`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.onAction(EditProfileUiAction.UpdateName("Mykola"))
            viewModel.onAction(EditProfileUiAction.UpdateSurname("Marchenko"))
            viewModel.onAction(EditProfileUiAction.UpdateEmail("mykola.marchenko@gmail.com"))
            viewModel.onAction(EditProfileUiAction.UpdatePhone("+380671234567"))
            viewModel.onAction(EditProfileUiAction.UpdateDateOfBirthday("18.07.1993"))

            assertTrue(viewModel.uiState.value.isSaveButtonEnabled)
        }
    }

    @Nested
    @DisplayName("Save Profile")
    inner class SaveProfile {

        @Test
        fun `GIVEN valid data WHEN saving profile THEN emits NavigateBack side effect`() = runTest {
            coEvery { saveUserUseCase(any()) } returns ValidationResult.Success
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.sideEffect.test {
                viewModel.onAction(EditProfileUiAction.UpdateName("Natalia"))
                viewModel.onAction(EditProfileUiAction.UpdateSurname("Lysenko"))
                viewModel.onAction(EditProfileUiAction.UpdateEmail("natalia.lysenko@ukr.net"))
                viewModel.onAction(EditProfileUiAction.UpdatePhone("+380991234567"))
                viewModel.onAction(EditProfileUiAction.UpdateDateOfBirthday("25.12.1989"))
                viewModel.onAction(EditProfileUiAction.SaveProfile)
                advanceUntilIdle()

                assertEquals(EditProfileSideEffect.NavigateBack, awaitItem())
                coVerify(exactly = 1) { saveUserUseCase(any()) }
            }
        }

        @Test
        fun `GIVEN validation errors WHEN saving profile THEN updates state with errors`() =
            runTest {
                val expectedErrors = mapOf(UserField.NAME to ValidationError.NameRequired)
                coEvery { saveUserUseCase(any()) } returns ValidationResult.Error(expectedErrors)
                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                viewModel.onAction(EditProfileUiAction.UpdateName("O"))
                viewModel.onAction(EditProfileUiAction.UpdateSurname("Oliynyk"))
                viewModel.onAction(EditProfileUiAction.UpdateEmail("oleh.oliynyk@gmail.com"))
                viewModel.onAction(EditProfileUiAction.UpdatePhone("+380631234567"))
                viewModel.onAction(EditProfileUiAction.UpdateDateOfBirthday("03.09.1994"))
                viewModel.onAction(EditProfileUiAction.SaveProfile)
                advanceUntilIdle()

                assertEquals(expectedErrors, viewModel.uiState.value.validationErrors)
                assertFalse(viewModel.uiState.value.isLoading)
            }
    }

    @Nested
    @DisplayName("Date Picker")
    inner class DatePicker {

        @Test
        fun `GIVEN show date picker action WHEN triggered THEN updates state to show picker`() =
            runTest {
                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                viewModel.onAction(EditProfileUiAction.ShowDatePicker)

                assertTrue(viewModel.uiState.value.showDatePicker)
            }

        @Test
        fun `GIVEN hide date picker action WHEN triggered THEN updates state to hide picker`() =
            runTest {
                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                viewModel.onAction(EditProfileUiAction.ShowDatePicker)
                viewModel.onAction(EditProfileUiAction.HideDatePicker)

                assertFalse(viewModel.uiState.value.showDatePicker)
            }
    }

    @Nested
    @DisplayName("Image Picker")
    inner class ImagePicker {

        @Test
        fun `GIVEN show image picker action WHEN triggered THEN emits OpenImagePicker side effect`() =
            runTest {
                val viewModel = createViewModel()

                viewModel.sideEffect.test {
                    viewModel.onAction(EditProfileUiAction.ShowImagePicker)
                    advanceUntilIdle()
                    assertEquals(EditProfileSideEffect.OpenImagePicker, awaitItem())
                }
            }

        @Test
        fun `GIVEN avatar URI WHEN updating avatar THEN updates state with new URI`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.onAction(EditProfileUiAction.UpdateAvatar("content://avatar.jpg"))

            assertEquals("content://avatar.jpg", viewModel.uiState.value.avatarUri)
        }
    }

    private fun createUser(
        name: String = "Serhii",
        surname: String = "Rudenko",
        phone: String = "+380501234567",
        email: String = "serhii.rudenko@gmail.com",
        dateOfBirthday: String = "28.02.1990",
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