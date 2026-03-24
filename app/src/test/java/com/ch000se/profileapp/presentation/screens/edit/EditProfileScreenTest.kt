package com.ch000se.profileapp.presentation.screens.edit

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.testing.AbstractComposableTest
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class EditProfileScreenTest : AbstractComposableTest() {

    override fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit = {
        ProfileAppTheme(content = content)
    }

    private lateinit var mockOnAction: (EditProfileUiAction) -> Unit

    @Before
    fun setUp() {
        mockOnAction = mockk(relaxed = true)
    }

    // region User Interactions

    @Test
    fun `GIVEN save button enabled WHEN save button clicked THEN triggers save action`() = runTest {
        val uiState = createEditProfileUiState(isSaveButtonEnabled = true)

        setContent {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = false,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = mockOnAction,
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.save)).performClick()

        verify(exactly = 1) { mockOnAction(EditProfileUiAction.SaveProfile) }
        takeScreenshot()
    }

    // endregion

    // region Content Display

    @Test
    fun `GIVEN edit mode WHEN screen displayed THEN shows edit profile title`() = runTest {
        val uiState = createEditProfileUiState()

        setContent {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = false,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = {},
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.edit_profile)).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN create mode WHEN screen displayed THEN shows create profile title`() = runTest {
        val uiState = EditProfileUiState()

        setContent {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = true,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = {},
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.create_profile)).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN save button disabled WHEN screen displayed THEN save button is disabled`() = runTest {
        val uiState = createEditProfileUiState(isSaveButtonEnabled = false)

        setContent {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = false,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = {},
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.save)).assertIsNotEnabled()
        takeScreenshot()
    }

    @Test
    fun `GIVEN save button enabled WHEN screen displayed THEN save button is enabled`() = runTest {
        val uiState = createEditProfileUiState(isSaveButtonEnabled = true)

        setContent {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = false,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = {},
                onNavigateBack = {}
            )
        }

        onNodeWithText(context.getString(R.string.save)).assertIsEnabled()
        takeScreenshot()
    }

    // endregion

    // region Animation Tests

    @Test
    fun `GIVEN loading state WHEN time advances THEN loading animation progresses`() = runTest {
        val uiState = createEditProfileUiState(isLoading = true)

        setContent(autoAdvance = false) {
            EditProfileScreenContent(
                uiState = uiState,
                isCreateMode = false,
                windowSize = WindowWidthSizeClass.Compact,
                onAction = {},
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

    private fun createEditProfileUiState(
        name: String = "Oleksandr",
        surname: String = "Kovalenko",
        phone: String = "+380501234567",
        email: String = "oleksandr.kovalenko@gmail.com",
        dateOfBirthday: String = "15.03.1990",
        avatarUri: String = "",
        isLoading: Boolean = false,
        showDatePicker: Boolean = false,
        isSaveButtonEnabled: Boolean = true
    ) = EditProfileUiState(
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatarUri = avatarUri,
        isLoading = isLoading,
        showDatePicker = showDatePicker,
        isSaveButtonEnabled = isSaveButtonEnabled
    )
}