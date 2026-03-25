package com.ch000se.profileapp.presentation.screens.contactDetail

import com.ch000se.profileapp.core_ui.testing.AbstractViewModelTest
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.GetContactByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ContactDetailViewModel")
class ContactDetailViewModelTest : AbstractViewModelTest<ContactDetailViewModel>() {

    @MockK
    private lateinit var getContactByIdUseCase: GetContactByIdUseCase

    private val testContactId = "test-contact-id"

    override fun createViewModel() = ContactDetailViewModel(
        getContactByIdUseCase = getContactByIdUseCase,
        contactId = testContactId
    )

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN loads contact automatically`() = runTest {
            val expectedContact = createContact(id = testContactId)
            coEvery { getContactByIdUseCase(testContactId) } returns expectedContact

            createViewModel()
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
        fun `GIVEN use case succeeds WHEN loading user THEN updates state with contact`() = runTest {
            val expectedContact = createContact(id = testContactId, name = "Yulia", surname = "Melnyk")
            coEvery { getContactByIdUseCase(testContactId) } returns expectedContact

            createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(expectedContact, viewModel.uiState.value.contact)
            assertNull(viewModel.uiState.value.error)
        }

        @Test
        fun `GIVEN use case fails WHEN loading user THEN sets error state`() = runTest {
            val expectedErrorMessage = "Contact not found"
            coEvery { getContactByIdUseCase(testContactId) } throws Exception(expectedErrorMessage)

            createViewModel()
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

            createViewModel()
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
