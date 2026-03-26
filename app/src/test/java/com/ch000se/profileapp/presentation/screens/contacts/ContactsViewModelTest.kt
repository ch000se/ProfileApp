@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ch000se.profileapp.presentation.screens.contacts

import com.ch000se.profileapp.core.coroutines.TestAppDispatchers
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.DeleteContactUseCase
import com.ch000se.profileapp.domain.usecases.SearchContactsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ContactsViewModel")
class ContactsViewModelTest {

    @MockK
    private lateinit var searchContactsUseCase: SearchContactsUseCase

    @MockK
    private lateinit var deleteContactUseCase: DeleteContactUseCase

    private val testDispatcher = StandardTestDispatcher()
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

    private fun createViewModel(): ContactsViewModel {
        coEvery { searchContactsUseCase(any(), any()) } returns emptyList()
        return ContactsViewModel(
            searchContactsUseCase = searchContactsUseCase,
            deleteContactUseCase = deleteContactUseCase,
            dispatchers = testDispatchers
        )
    }

    private fun TestScope.collectState(viewModel: ContactsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN all category filters are selected`() =
            runTest {
                val viewModel = createViewModel()
                collectState(viewModel)

                assertEquals(3, viewModel.uiState.value.categoryFilters.size)
                assertTrue(viewModel.uiState.value.categoryFilters.all { it.isSelected })
            }

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN query is empty`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)

            assertEquals("", viewModel.uiState.value.query)
        }
    }

    @Nested
    @DisplayName("Search Contacts")
    inner class SearchContacts {

        @Test
        fun `GIVEN valid query WHEN searching contacts THEN updates contacts list`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            val expectedContacts = listOf(createContact(name = "Dmytro"))
            coEvery { searchContactsUseCase(any(), any()) } returns expectedContacts
            viewModel.onAction(ContactsUiAction.SearchContacts("Dmytro"))
            advanceTimeBy(300)
            advanceUntilIdle()

            assertEquals("Dmytro", viewModel.uiState.value.query)
            assertEquals(1, viewModel.uiState.value.contacts.size)
        }

        @Test
        fun `GIVEN search debounce WHEN typing quickly THEN waits before searching`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.onAction(ContactsUiAction.SearchContacts("T"))
            viewModel.onAction(ContactsUiAction.SearchContacts("Ta"))
            viewModel.onAction(ContactsUiAction.SearchContacts("Tar"))
            advanceTimeBy(300)
            advanceUntilIdle()

            assertEquals("Tar", viewModel.uiState.value.query)
        }

        @Test
        fun `GIVEN use case fails WHEN searching contacts THEN sets error state`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            coEvery { searchContactsUseCase(any(), any()) } throws Exception("Search failed")
            viewModel.onAction(ContactsUiAction.SearchContacts("test"))
            advanceTimeBy(300)
            advanceUntilIdle()

            assertNotNull(viewModel.uiState.value.error)
        }
    }

    @Nested
    @DisplayName("Category Filter")
    inner class CategoryFilter {

        @Test
        fun `GIVEN category toggled WHEN toggle action THEN updates filter`() = runTest {
            val viewModel = createViewModel()
            collectState(viewModel)
            advanceUntilIdle()

            viewModel.onAction(ContactsUiAction.ToggleCategoryFilter(ContactCategory.FAMILY))
            advanceUntilIdle()

            val familyFilter = viewModel.uiState.value.categoryFilters
                .first { it.category == ContactCategory.FAMILY }
            assertFalse(familyFilter.isSelected)
        }

        @Test
        fun `GIVEN all categories deselected WHEN toggling THEN shows empty contacts list`() =
            runTest {
                val viewModel = createViewModel()
                collectState(viewModel)
                advanceUntilIdle()

                val contact = createContact()
                coEvery { searchContactsUseCase(any(), any()) } returns listOf(contact)
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
        fun `GIVEN delete succeeds WHEN deleting contact THEN removes contact from list`() =
            runTest {
                val contactToDelete = createContact(id = "1")
                val contactToKeep = createContact(id = "2")
                coEvery { searchContactsUseCase(any(), any()) } returns listOf(
                    contactToDelete,
                    contactToKeep
                )
                coEvery { deleteContactUseCase("1") } returns Unit

                val viewModel = ContactsViewModel(
                    searchContactsUseCase = searchContactsUseCase,
                    deleteContactUseCase = deleteContactUseCase,
                    dispatchers = testDispatchers
                )
                collectState(viewModel)
                advanceUntilIdle()

                viewModel.onAction(ContactsUiAction.DeleteContact("1"))
                advanceUntilIdle()

                assertEquals(1, viewModel.uiState.value.contacts.size)
                assertEquals("2", viewModel.uiState.value.contacts.first().id)
                assertFalse(viewModel.uiState.value.isDeleting)
            }

        @Test
        fun `GIVEN delete fails WHEN deleting contact THEN sets error and keeps contact`() =
            runTest {
                val contact = createContact(id = "1")
                coEvery { searchContactsUseCase(any(), any()) } returns listOf(contact)
                coEvery { deleteContactUseCase("1") } throws Exception("Delete failed")

                val viewModel = ContactsViewModel(
                    searchContactsUseCase = searchContactsUseCase,
                    deleteContactUseCase = deleteContactUseCase,
                    dispatchers = testDispatchers
                )
                collectState(viewModel)
                advanceUntilIdle()

                viewModel.onAction(ContactsUiAction.DeleteContact("1"))
                advanceUntilIdle()

                assertEquals(1, viewModel.uiState.value.contacts.size)
                assertNotNull(viewModel.uiState.value.error)
                assertFalse(viewModel.uiState.value.isDeleting)
            }

        @Test
        fun `GIVEN deleting contact WHEN action triggered THEN verifies use case is called`() =
            runTest {
                coEvery {
                    searchContactsUseCase(
                        any(),
                        any()
                    )
                } returns listOf(createContact(id = "1"))
                coEvery { deleteContactUseCase("1") } returns Unit

                val viewModel = ContactsViewModel(
                    searchContactsUseCase = searchContactsUseCase,
                    deleteContactUseCase = deleteContactUseCase,
                    dispatchers = testDispatchers
                )
                collectState(viewModel)
                advanceUntilIdle()

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