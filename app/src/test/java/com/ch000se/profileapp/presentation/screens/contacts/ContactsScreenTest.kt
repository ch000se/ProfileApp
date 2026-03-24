package com.ch000se.profileapp.presentation.screens.contacts

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.model.UiText
import com.ch000se.profileapp.core_ui.testing.AbstractComposableTest
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.presentation.common.model.CategoryUiModel
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ContactsScreenTest : AbstractComposableTest() {

    override fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit = {
        ProfileAppTheme(content = content)
    }

    private lateinit var mockOnNavigateToAddContact: () -> Unit
    private lateinit var mockOnToggleCategory: (ContactCategory) -> Unit

    @Before
    fun setUp() {
        mockOnNavigateToAddContact = mockk(relaxed = true)
        mockOnToggleCategory = mockk(relaxed = true)
    }

    // region User Interactions

    @Test
    fun `GIVEN contacts list WHEN add contact FAB clicked THEN navigates to add contact screen`() = runTest {
        val uiState = ContactsUiState(
            contacts = createContactsList(),
            categoryFilters = createCategoryFilters()
        )

        setContent {
            ContactsScreenContent(
                uiState = uiState,
                onNavigateToAddContact = mockOnNavigateToAddContact,
                onNavigateToContactDetail = {},
                onNavigateToProfile = {},
                onSearchQuery = {},
                onToggleCategory = {},
                onDeleteContact = {}
            )
        }

        onNodeWithContentDescription(context.getString(R.string.add_contact)).performClick()

        verify(exactly = 1) { mockOnNavigateToAddContact() }
        takeScreenshot()
    }

    @Test
    fun `GIVEN category filter WHEN filter chip clicked THEN toggles category`() = runTest {
        val expectedCategory = ContactCategory.FRIENDS
        val uiState = ContactsUiState(
            contacts = emptyList(),
            categoryFilters = createCategoryFilters()
        )

        setContent {
            ContactsScreenContent(
                uiState = uiState,
                onNavigateToAddContact = {},
                onNavigateToContactDetail = {},
                onNavigateToProfile = {},
                onSearchQuery = {},
                onToggleCategory = mockOnToggleCategory,
                onDeleteContact = {},
                initialSearchSectionExpanded = true
            )
        }

        onNodeWithText(context.getString(R.string.category_friends)).performClick()

        verify(exactly = 1) { mockOnToggleCategory(expectedCategory) }
        takeScreenshot()
    }

    // endregion

    // region Content Display

    @Test
    fun `GIVEN contacts list WHEN screen displayed THEN shows all contacts`() = runTest {
        val expectedName1 = "Mariia"
        val expectedName2 = "Andriy"
        val uiState = ContactsUiState(
            contacts = createContactsList(),
            categoryFilters = createCategoryFilters()
        )

        setContent {
            ContactsScreenContent(
                uiState = uiState,
                onNavigateToAddContact = {},
                onNavigateToContactDetail = {},
                onNavigateToProfile = {},
                onSearchQuery = {},
                onToggleCategory = {},
                onDeleteContact = {}
            )
        }

        onNodeWithText(expectedName1, substring = true).assertIsDisplayed()
        onNodeWithText(expectedName2, substring = true).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN empty contacts WHEN screen displayed THEN shows no contacts message`() = runTest {
        val uiState = ContactsUiState(
            contacts = emptyList(),
            categoryFilters = emptyList()
        )

        setContent {
            ContactsScreenContent(
                uiState = uiState,
                onNavigateToAddContact = {},
                onNavigateToContactDetail = {},
                onNavigateToProfile = {},
                onSearchQuery = {},
                onToggleCategory = {},
                onDeleteContact = {}
            )
        }

        onNodeWithText(context.getString(R.string.no_contacts)).assertIsDisplayed()
        takeScreenshot()
    }

    // endregion

    // region Animation Tests

    @Test
    fun `GIVEN deleting state WHEN time advances THEN loading animation progresses`() = runTest {
        val uiState = ContactsUiState(
            contacts = createContactsList(),
            categoryFilters = createCategoryFilters(),
            isDeleting = true
        )

        setContent(autoAdvance = false) {
            ContactsScreenContent(
                uiState = uiState,
                onNavigateToAddContact = {},
                onNavigateToContactDetail = {},
                onNavigateToProfile = {},
                onSearchQuery = {},
                onToggleCategory = {},
                onDeleteContact = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()
    }

    // endregion

    private fun createContactsList() = listOf(
        Contact(
            id = "1",
            name = "Mariia",
            surname = "Kovalenko",
            phone = "+380501111111",
            email = "mariia.kovalenko@gmail.com",
            dateOfBirthday = "10.01.1992",
            avatar = "",
            categories = listOf(ContactCategory.FAMILY)
        ),
        Contact(
            id = "2",
            name = "Andriy",
            surname = "Shevchenko",
            phone = "+380502222222",
            email = "andriy.shevchenko@gmail.com",
            dateOfBirthday = "20.05.1988",
            avatar = "",
            categories = listOf(ContactCategory.WORK)
        )
    )

    private fun createCategoryFilters() = listOf(
        CategoryUiModel(
            category = ContactCategory.FAMILY,
            label = UiText.StringResource(R.string.category_family),
            isSelected = false
        ),
        CategoryUiModel(
            category = ContactCategory.FRIENDS,
            label = UiText.StringResource(R.string.category_friends),
            isSelected = false
        ),
        CategoryUiModel(
            category = ContactCategory.WORK,
            label = UiText.StringResource(R.string.category_work),
            isSelected = false
        )
    )
}