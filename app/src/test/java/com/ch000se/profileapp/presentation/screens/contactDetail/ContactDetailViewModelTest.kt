@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.presentation.screens.contactDetail

import com.ch000se.profileapp.core.coroutines.TestAppDispatchers
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.GetContactByIdUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ContactDetailViewModel")
class ContactDetailViewModelTest {

    @MockK
    private lateinit var getContactByIdUseCase: GetContactByIdUseCase

    private val testContactId = "test-contact-id"

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

    private fun createViewModel(contactId: String = testContactId) = ContactDetailViewModel(
        getContactByIdUseCase = getContactByIdUseCase,
        dispatchers = testDispatchers,
        contactId = contactId
    )

    private fun TestScope.collectState(viewModel: ContactDetailViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN loads contact automatically`() =
            runTest {
                val expectedContact = createContact(id = testContactId)
                coEvery { getContactByIdUseCase(testContactId) } returns expectedContact

                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                assertFalse(viewModel.uiState.value.isLoading)
                assertNotNull(viewModel.uiState.value.contact)
                coVerify(exactly = 1) { getContactByIdUseCase(testContactId) }
            }
    }

    @Nested
    @DisplayName("Load User")
    inner class LoadUser {

        @Test
        fun `GIVEN use case succeeds WHEN loading user THEN updates state with contact`() =
            runTest {
                val expectedContact =
                    createContact(id = testContactId, name = "Yulia", surname = "Melnyk")
                coEvery { getContactByIdUseCase(testContactId) } returns expectedContact

                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                assertFalse(viewModel.uiState.value.isLoading)
                assertEquals(expectedContact, viewModel.uiState.value.contact)
                assertNull(viewModel.uiState.value.error)
            }

        @Test
        fun `GIVEN use case fails WHEN loading user THEN sets error state`() = runTest {
            val expectedErrorMessage = "Contact not found"
            coEvery { getContactByIdUseCase(testContactId) } throws Exception(expectedErrorMessage)

            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.contact)
            assertEquals(expectedErrorMessage, viewModel.uiState.value.error)
        }

        @Test
        fun `GIVEN contact loaded WHEN data retrieved THEN contact data is correct`() = runTest {
            val expectedContact = createContact(
                id = testContactId,
                name = "Andrii",
                surname = "Bondarenko",
                phone = "+380931112233"
            )
            coEvery { getContactByIdUseCase(testContactId) } returns expectedContact

            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            val contact = viewModel.uiState.value.contact
            assertNotNull(contact)
            assertEquals("Andrii", contact?.name)
            assertEquals("Bondarenko", contact?.surname)
            assertEquals("+380931112233", contact?.phone)
        }
    }

    private fun createContact(
        id: String = "1",
        name: String = "Iryna",
        surname: String = "Tkachenko",
        phone: String = "+380661234567",
        email: String = "iryna.tkachenko@gmail.com",
        dateOfBirthday: String = "05.11.1995",
        avatar: String = "",
        categories: List<ContactCategory> = listOf(ContactCategory.FRIENDS)
    ) = Contact(
        id = id,
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatar = avatar,
        categories = categories
    )
}