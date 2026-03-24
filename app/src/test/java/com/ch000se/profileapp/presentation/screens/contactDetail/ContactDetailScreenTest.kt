package com.ch000se.profileapp.presentation.screens.contactDetail

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.testing.AbstractComposableTest
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ContactDetailScreenTest : AbstractComposableTest() {

    override fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit = {
        ProfileAppTheme(content = content)
    }

    private lateinit var mockOnNavigateBack: () -> Unit

    @Before
    fun setUp() {
        mockOnNavigateBack = mockk(relaxed = true)
    }

    // region User Interactions

    @Test
    fun `GIVEN contact loaded WHEN back button clicked THEN navigates back`() = runTest {
        val contact = createContact()

        setContent {
            ContactDetailScreenContent(
                contact = contact,
                isLoading = false,
                windowSize = WindowWidthSizeClass.Compact,
                onNavigateBack = mockOnNavigateBack
            )
        }

        onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        verify(exactly = 1) { mockOnNavigateBack() }
        takeScreenshot()
    }

    // endregion

    // region Content Display

    @Test
    fun `GIVEN contact loaded WHEN screen displayed THEN shows contact information`() = runTest {
        val expectedName = "Oleksandr"
        val expectedSurname = "Kovalenko"
        val contact = createContact(
            name = expectedName,
            surname = expectedSurname
        )

        setContent {
            ContactDetailScreenContent(
                contact = contact,
                isLoading = false,
                windowSize = WindowWidthSizeClass.Compact,
                onNavigateBack = {}
            )
        }

        onNodeWithText(expectedName, substring = true).assertIsDisplayed()
        onNodeWithText(expectedSurname, substring = true).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN contact is null WHEN screen displayed THEN shows not found message`() = runTest {
        setContent {
            ContactDetailScreenContent(
                contact = null,
                isLoading = false,
                windowSize = WindowWidthSizeClass.Compact,
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.profile_not_found)).assertIsDisplayed()
        takeScreenshot()
    }

    // endregion

    // region Animation Tests

    @Test
    fun `GIVEN loading state WHEN time advances THEN loading animation progresses`() = runTest {
        setContent(autoAdvance = false) {
            ContactDetailScreenContent(
                contact = null,
                isLoading = true,
                windowSize = WindowWidthSizeClass.Compact,
                onNavigateBack = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()
    }

    // endregion

    private fun createContact(
        id: String = "1",
        name: String = "Oleksandr",
        surname: String = "Kovalenko",
        phone: String = "+380501234567",
        email: String = "oleksandr.kovalenko@gmail.com",
        dateOfBirthday: String = "15.03.1990",
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