package com.ch000se.profileapp.presentation.screens.contacts

import com.ch000se.profileapp.core_ui.testing.AbstractViewModelTest
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.DeleteContactUseCase
import com.ch000se.profileapp.domain.usecases.SearchContactsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ContactsViewModel")
class ContactsViewModelTest : AbstractViewModelTest<ContactsViewModel>() {

    @MockK
    private lateinit var searchContactsUseCase: SearchContactsUseCase

    @MockK
    private lateinit var deleteContactUseCase: DeleteContactUseCase

    override fun createViewModel() = ContactsViewModel(
        searchContactsUseCase = searchContactsUseCase,
        deleteContactUseCase = deleteContactUseCase
    )

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN all category filters are selected`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns emptyList()

            createViewModel()

            assertEquals(3, viewModel.uiState.value.categoryFilters.size)
            assertTrue(viewModel.uiState.value.categoryFilters.all { it.isSelected })
        }

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN query is empty`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns emptyList()

            createViewModel()

            assertEquals("", viewModel.uiState.value.query)
        }
    }

    @Nested
    @DisplayName("Search Contacts")
    inner class SearchContacts {

        @Test
        fun `GIVEN valid query WHEN searching contacts THEN updates contacts list`() = runTest {
            val expectedContacts = listOf(createContact(name = "Dmytro"))
            coEvery { searchContactsUseCase(any(), any()) } returns expectedContacts

            createViewModel()
            viewModel.onAction(ContactsUiAction.SearchContacts("Dmytro"))
            advanceTimeBy(400)
            advanceUntilIdle()

            assertEquals("Dmytro", viewModel.uiState.value.query)
            assertEquals(1, viewModel.uiState.value.contacts.size)
        }

        @Test
        fun `GIVEN search debounce WHEN typing quickly THEN waits before searching`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns emptyList()

            createViewModel()
            viewModel.onAction(ContactsUiAction.SearchContacts("T"))
            viewModel.onAction(ContactsUiAction.SearchContacts("Ta"))
            viewModel.onAction(ContactsUiAction.SearchContacts("Tar"))
            advanceTimeBy(400)
            advanceUntilIdle()

            assertEquals("Tar", viewModel.uiState.value.query)
        }

        @Test
        fun `GIVEN use case fails WHEN searching contacts THEN sets error state`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } throws Exception("Search failed")

            createViewModel()
            viewModel.onAction(ContactsUiAction.SearchContacts("test"))
            advanceTimeBy(400)
            advanceUntilIdle()

            assertNotNull(viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("Category Filter")
    inner class CategoryFilter {

        @Test
        fun `GIVEN category toggled WHEN toggle action THEN updates filter`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns emptyList()

            createViewModel()
            advanceTimeBy(400)
            viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(ContactCategory.FAMILY))
            advanceUntilIdle()

            val familyFilter = viewModel.uiState.value.categoryFilters
                .first { it.category == ContactCategory.FAMILY }
            assertFalse(familyFilter.isSelected)
        }

        @Test
        fun `GIVEN all categories deselected WHEN toggling THEN shows empty contacts list`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns listOf(createContact())

            createViewModel()
            advanceTimeBy(400)

            viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(ContactCategory.FAMILY))
            viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(ContactCategory.FRIENDS))
            viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(ContactCategory.WORK))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.contacts.isEmpty())
        }
    }

    @Nested
    @DisplayName("Delete Contact")
    inner class DeleteContact {

        @Test
        fun `GIVEN delete succeeds WHEN deleting contact THEN removes contact from list`() = runTest {
            val contactToDelete = createContact(id = "1")
            val contactToKeep = createContact(id = "2")
            coEvery { searchContactsUseCase(any(), any()) } returns listOf(contactToDelete, contactToKeep)
            coEvery { deleteContactUseCase("1") } returns Unit

            createViewModel()
            advanceTimeBy(400)

            viewModel.onAction(ContactsUiAction.DeleteContact("1"))
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.contacts.size)
            assertEquals("2", viewModel.uiState.value.contacts.first().id)
            assertFalse(viewModel.uiState.value.isDeleting)
        }

        @Test
        fun `GIVEN delete fails WHEN deleting contact THEN sets error and keeps contact`() = runTest {
            val contact = createContact(id = "1")
            coEvery { searchContactsUseCase(any(), any()) } returns listOf(contact)
            coEvery { deleteContactUseCase("1") } throws Exception("Delete failed")

            createViewModel()
            advanceTimeBy(400)

            viewModel.onAction(ContactsUiAction.DeleteContact("1"))
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.contacts.size)
            assertNotNull(viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isDeleting)
        }

        @Test
        fun `GIVEN deleting contact WHEN action triggered THEN verifies use case is called`() = runTest {
            coEvery { searchContactsUseCase(any(), any()) } returns listOf(createContact(id = "1"))
            coEvery { deleteContactUseCase("1") } returns Unit

            createViewModel()
            advanceTimeBy(400)

            viewModel.onAction(ContactsUiAction.DeleteContact("1"))
            advanceUntilIdle()

            coVerify(exactly = 1) { deleteContactUseCase("1") }
        }
    }

    private fun createContact(
        id: String = "1",
        name: String = "Taras",
        surname: String = "Kovalenko",
        phone: String = "+380671234567",
        email: String = "taras.kovalenko@ukr.net",
        dateOfBirthday: String = "22.08.1988",
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
