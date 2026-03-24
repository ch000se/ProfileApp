package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.repository.ContactRepository
import com.ch000se.profileapp.domain.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("LogoutUseCase")
class LogoutUseCaseTest {

    @MockK(relaxed = true)
    private lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    private lateinit var contactRepository: ContactRepository

    private lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        logoutUseCase = LogoutUseCase(userRepository, contactRepository)
    }

    @Nested
    @DisplayName("invoke()")
    inner class Invoke {

        @Test
        fun `GIVEN logout is requested WHEN invoke THEN both repositories are called`() = runTest {
            logoutUseCase.invoke()

            coVerify(exactly = 1) { contactRepository.deleteAllContacts() }
            coVerify(exactly = 1) { userRepository.deleteUser() }
        }

        @Test
        fun `GIVEN logout is requested WHEN invoke THEN deleteAllContacts is called before deleteUser`() = runTest {
            logoutUseCase.invoke()

            coVerify(ordering = Ordering.ORDERED) {
                contactRepository.deleteAllContacts()
                userRepository.deleteUser()
            }
        }
    }
}
