package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.repository.UserRepository
import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.UserValidator
import com.ch000se.profileapp.domain.validation.ValidationError
import com.ch000se.profileapp.domain.validation.ValidationResult
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveUserUseCase")
class SaveUserUseCaseTest {

    @MockK(relaxed = true)
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userValidator: UserValidator

    private lateinit var saveUserUseCase: SaveUserUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        saveUserUseCase = SaveUserUseCase(userRepository, userValidator)
    }

    @Nested
    @DisplayName("invoke()")
    inner class Invoke {

        @Test
        fun `GIVEN valid user WHEN invoke THEN validation passes and user is saved`() = runTest {
            val validUser = createUser()
            every { userValidator.validate(validUser) } returns ValidationResult.Success

            val result = saveUserUseCase.invoke(validUser)

            coVerify(exactly = 1) { userValidator.validate(validUser) }
            coVerify(exactly = 1) { userRepository.saveUser(validUser) }
            assertTrue(result is ValidationResult.Success)
        }

        @Test
        fun `GIVEN invalid user WHEN invoke THEN validation fails and user is not saved`() = runTest {
            val invalidUser = createUser(name = "", email = "invalid-email")
            val expectedErrors = mapOf(
                UserField.NAME to ValidationError.NameRequired,
                UserField.EMAIL to ValidationError.EmailInvalid
            )
            val expectedResult = ValidationResult.Error(expectedErrors)
            every { userValidator.validate(invalidUser) } returns expectedResult

            val result = saveUserUseCase.invoke(invalidUser)

            coVerify(exactly = 1) { userValidator.validate(invalidUser) }
            coVerify(exactly = 0) { userRepository.saveUser(any()) }
            assertTrue(result is ValidationResult.Error)
            assertEquals(expectedErrors, (result as ValidationResult.Error).errors)
        }

        @Test
        fun `GIVEN validation error WHEN invoke THEN returns the error result from validator`() = runTest {
            val user = createUser()
            val expectedErrors = mapOf(UserField.PHONE to ValidationError.PhoneInvalid)
            val expectedResult = ValidationResult.Error(expectedErrors)
            every { userValidator.validate(user) } returns expectedResult

            val result = saveUserUseCase.invoke(user)

            assertEquals(expectedResult, result)
            coVerify(exactly = 0) { userRepository.saveUser(any()) }
        }
    }

    private fun createUser(
        name: String = "Dmytro",
        surname: String = "Melnyk",
        phone: String = "+380661234567",
        email: String = "dmytro.melnyk@gmail.com",
        dateOfBirthday: String = "1993-05-12",
        avatar: String = "https://example.com/avatar.jpg"
    ) = User(
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatar = avatar
    )
}
