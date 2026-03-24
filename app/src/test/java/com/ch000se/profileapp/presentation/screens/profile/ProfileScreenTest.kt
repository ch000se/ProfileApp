package com.ch000se.profileapp.presentation.screens.profile

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.testing.AbstractComposableTest
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ProfileScreenTest : AbstractComposableTest() {

    override fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit = {
        ProfileAppTheme(content = content)
    }

    private lateinit var mockOnNavigateToEdit: () -> Unit
    private lateinit var mockOnNavigateBack: () -> Unit

    @Before
    fun setUp() {
        mockOnNavigateToEdit = mockk(relaxed = true)
        mockOnNavigateBack = mockk(relaxed = true)
    }

    // region User Interactions

    @Test
    fun `GIVEN user loaded WHEN back button clicked THEN navigates back`() = runTest {
        val user = createUser()

        setContent {
            ProfileScreenContent(
                user = user,
                isLoading = false,
                showLogoutDialog = false,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = {},
                onBackClick = mockOnNavigateBack,
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
            )
        }

        onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        verify(exactly = 1) { mockOnNavigateBack() }
        takeScreenshot()
    }

    @Test
    fun `GIVEN user loaded WHEN edit FAB clicked THEN navigates to edit screen`() = runTest {
        val user = createUser()

        setContent {
            ProfileScreenContent(
                user = user,
                isLoading = false,
                showLogoutDialog = false,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = mockOnNavigateToEdit,
                onBackClick = {},
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
            )
        }

        onNodeWithContentDescription(context.getString(R.string.edit_profile)).performClick()

        verify(exactly = 1) { mockOnNavigateToEdit() }
        takeScreenshot()
    }

    // endregion

    // region Content Display

    @Test
    fun `GIVEN user loaded WHEN screen displayed THEN shows user information`() = runTest {
        val expectedName = "Oleksandr"
        val expectedSurname = "Kovalenko"
        val user = createUser(name = expectedName, surname = expectedSurname)

        setContent {
            ProfileScreenContent(
                user = user,
                isLoading = false,
                showLogoutDialog = false,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = {},
                onBackClick = {},
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
            )
        }

        onNodeWithText(expectedName, substring = true).assertIsDisplayed()
        onNodeWithText(expectedSurname, substring = true).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN user is null WHEN screen displayed THEN shows not found message`() = runTest {
        setContent {
            ProfileScreenContent(
                user = null,
                isLoading = false,
                showLogoutDialog = false,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = {},
                onBackClick = {},
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
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
            ProfileScreenContent(
                user = null,
                isLoading = true,
                showLogoutDialog = false,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = {},
                onBackClick = {},
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()
    }

    @Test
    fun `GIVEN logout dialog WHEN time advances THEN dialog animation completes`() = runTest {
        val user = createUser()

        setContent(autoAdvance = false) {
            ProfileScreenContent(
                user = user,
                isLoading = false,
                showLogoutDialog = true,
                windowSize = WindowWidthSizeClass.Compact,
                onEditClick = {},
                onBackClick = {},
                onLogoutClick = {},
                onConfirmLogout = {},
                onDismissLogout = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(300)

        onNodeWithText(context.getString(R.string.logout), substring = true).assertIsDisplayed()
        takeScreenshot()
    }

    // endregion

    private fun createUser(
        name: String = "Oleksandr",
        surname: String = "Kovalenko",
        phone: String = "+380501234567",
        email: String = "oleksandr.kovalenko@gmail.com",
        dateOfBirthday: String = "15.03.1990",
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