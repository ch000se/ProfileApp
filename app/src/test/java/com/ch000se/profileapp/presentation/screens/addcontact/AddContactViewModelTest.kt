package com.ch000se.profileapp.presentation.screens.addcontact

import app.cash.turbine.test
import com.ch000se.profileapp.core_ui.testing.AbstractViewModelTest
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.AddContactUseCase
import com.ch000se.profileapp.domain.usecases.GetRandomUsersUseCase
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
import java.io.IOException

@DisplayName("AddContactViewModel")
class AddContactViewModelTest : AbstractViewModelTest<AddContactViewModel>() {

    @MockK
    private lateinit var getRandomUsersUseCase: GetRandomUsersUseCase

    @MockK
    private lateinit var addContactUseCase: AddContactUseCase

    override fun createViewModel() = AddContactViewModel(
        getRandomUsersUseCase = getRandomUsersUseCase,
        addContactUseCase = addContactUseCase
    )

    @Nested
    @DisplayName("Initial State")
    inner class InitialState {

        @Test
        fun `GIVEN ViewModel created WHEN initialized THEN categories are initialized`() = runTest {
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(emptyList())

            createViewModel()
            advanceUntilIdle()

            assertEquals(3, viewModel.uiState.value.categories.size)
            assertTrue(viewModel.uiState.value.categories.all { !it.isSelected })
        }
    }

    @Nested
    @DisplayName("Load Random Users")
    inner class LoadRandomUsers {

        @Test
        fun `GIVEN use case succeeds WHEN loading random users THEN updates state with users`() = runTest {
            val expectedUsers = listOf(createContact(id = "1"), createContact(id = "2"))
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(expectedUsers)

            createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(2, viewModel.uiState.value.randomUsers.size)
        }

        @Test
        fun `GIVEN use case fails WHEN loading random users THEN sets error state`() = runTest {
            coEvery { getRandomUsersUseCase(any()) } returns Result.failure(IOException("Network error"))

            createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.error)
            assertTrue(viewModel.uiState.value.randomUsers.isEmpty())
        }
    }

    @Nested
    @DisplayName("Load More Users")
    inner class LoadMoreUsers {

        @Test
        fun `GIVEN use case succeeds WHEN loading more users THEN appends users to list`() = runTest {
            val initialUsers = listOf(createContact(id = "1"))
            val moreUsers = listOf(createContact(id = "2"))
            coEvery { getRandomUsersUseCase(any()) } returnsMany listOf(
                Result.success(initialUsers),
                Result.success(moreUsers)
            )

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.LoadMoreUsers)
            advanceUntilIdle()

            assertEquals(2, viewModel.uiState.value.randomUsers.size)
            assertFalse(viewModel.uiState.value.isLoadingMore)
        }

        @Test
        fun `GIVEN use case fails WHEN loading more users THEN keeps existing users`() = runTest {
            val initialUsers = listOf(createContact(id = "1"))
            coEvery { getRandomUsersUseCase(any()) } returnsMany listOf(
                Result.success(initialUsers),
                Result.failure(IOException())
            )

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.LoadMoreUsers)
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.randomUsers.size)
            assertFalse(viewModel.uiState.value.isLoadingMore)
        }
    }

    @Nested
    @DisplayName("User Selection")
    inner class UserSelection {

        @Test
        fun `GIVEN user and category selected WHEN selecting user THEN enables button`() = runTest {
            val testContact = createContact(id = "1")
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(listOf(testContact))

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.FAMILY))
            viewModel.onAction(AddContactUiAction.SelectUser(testContact))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.randomUsers.first().isSelected)
            assertTrue(viewModel.uiState.value.isButtonEnabled)
        }

        @Test
        fun `GIVEN different user selected WHEN selecting another user THEN deselects previous user`() = runTest {
            val contact1 = createContact(id = "1")
            val contact2 = createContact(id = "2")
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(listOf(contact1, contact2))

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.SelectUser(contact1))
            viewModel.onAction(AddContactUiAction.SelectUser(contact2))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.randomUsers[0].isSelected)
            assertTrue(viewModel.uiState.value.randomUsers[1].isSelected)
        }
    }

    @Nested
    @DisplayName("Category Selection")
    inner class CategorySelection {

        @Test
        fun `GIVEN category toggled WHEN toggle action THEN updates category selection state`() = runTest {
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(emptyList())

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.FAMILY))
            advanceUntilIdle()

            val familyCategory = viewModel.uiState.value.categories
                .first { it.category == ContactCategory.FAMILY }
            assertTrue(familyCategory.isSelected)
        }

        @Test
        fun `GIVEN category selected twice WHEN toggled THEN deselects category`() = runTest {
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(emptyList())

            createViewModel()
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.WORK))
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.WORK))
            advanceUntilIdle()

            val workCategory = viewModel.uiState.value.categories
                .first { it.category == ContactCategory.WORK }
            assertFalse(workCategory.isSelected)
        }
    }

    @Nested
    @DisplayName("Save Contact")
    inner class SaveContact {

        @Test
        fun `GIVEN user and category selected WHEN saving contact THEN emits NavigateBack side effect`() = runTest {
            val testContact = createContact()
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(listOf(testContact))
            coEvery { addContactUseCase(any()) } returns Unit

            createViewModel()
            advanceUntilIdle()

            viewModel.onAction(AddContactUiAction.SelectUser(testContact))
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.FRIENDS))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isButtonEnabled)

            viewModel.sideEffect.test {
                viewModel.onAction(AddContactUiAction.SaveContact)
                advanceUntilIdle()

                assertEquals(AddContactSideEffect.NavigateBack, awaitItem())
            }
            coVerify(exactly = 1) { addContactUseCase(any()) }
        }

        @Test
        fun `GIVEN save fails WHEN saving contact THEN emits ShowError side effect`() = runTest {
            val testContact = createContact()
            coEvery { getRandomUsersUseCase(any()) } returns Result.success(listOf(testContact))
            coEvery { addContactUseCase(any()) } throws Exception("Save failed")

            createViewModel()
            advanceUntilIdle()

            viewModel.onAction(AddContactUiAction.SelectUser(testContact))
            advanceUntilIdle()
            viewModel.onAction(AddContactUiAction.ToggleCategory(ContactCategory.FAMILY))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isButtonEnabled)

            viewModel.sideEffect.test {
                viewModel.onAction(AddContactUiAction.SaveContact)
                advanceUntilIdle()

                assertTrue(awaitItem() is AddContactSideEffect.ShowError)
            }
        }
    }

    private fun createContact(
        id: String = "1",
        name: String = "Oleksandr",
        surname: String = "Shevchenko",
        phone: String = "+380501234567",
        email: String = "oleksandr.shevchenko@gmail.com",
        dateOfBirthday: String = "15.03.1992",
        avatar: String = "",
        categories: List<ContactCategory> = emptyList()
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