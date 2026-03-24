package com.ch000se.profileapp.presentation.screens.addcontact

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.error.NetworkError
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

class AddContactScreenTest : AbstractComposableTest() {

    override fun wrapWithTheme(content: @Composable () -> Unit): @Composable () -> Unit = {
        ProfileAppTheme(content = content)
    }

    private lateinit var mockOnNavigateBack: () -> Unit
    private lateinit var mockOnAction: (AddContactUiAction) -> Unit

    @Before
    fun setUp() {
        mockOnNavigateBack = mockk(relaxed = true)
        mockOnAction = mockk(relaxed = true)
    }

    // region User Interactions

    @Test
    fun `GIVEN screen displayed WHEN back button clicked THEN navigates back`() = runTest {
        val uiState = createAddContactUiState()

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = mockOnNavigateBack,
                onAction = {}
            )
        }

        onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        verify(exactly = 1) { mockOnNavigateBack() }
        takeScreenshot()
    }

    @Test
    fun `GIVEN screen displayed WHEN refresh button clicked THEN refreshes users`() = runTest {
        val uiState = createAddContactUiState()

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = mockOnAction
            )
        }

        onNodeWithContentDescription(context.getString(R.string.refresh)).performClick()

        verify(exactly = 1) { mockOnAction(AddContactUiAction.RefreshUsers) }
        takeScreenshot()
    }

    @Test
    fun `GIVEN error state WHEN retry button clicked THEN refreshes users`() = runTest {
        val uiState = AddContactUiState(
            isLoading = false,
            error = NetworkError.NO_INTERNET
        )

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = mockOnAction
            )
        }

        onNodeWithText(context.getString(R.string.retry)).performClick()

        verify(exactly = 1) { mockOnAction(AddContactUiAction.RefreshUsers) }
        takeScreenshot()
    }

    // endregion

    // region Content Display

    @Test
    fun `GIVEN users loaded WHEN screen displayed THEN shows all users`() = runTest {
        val expectedName1 = "Mariia"
        val expectedName2 = "Andriy"
        val users = createRandomUsers()
        val uiState = createAddContactUiState(
            randomUsers = users.map { Selectable(it, false) }
        )

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        onNodeWithText(expectedName1, substring = true).assertIsDisplayed()
        onNodeWithText(expectedName2, substring = true).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN error state WHEN screen displayed THEN shows retry button`() = runTest {
        val uiState = AddContactUiState(
            isLoading = false,
            error = NetworkError.NO_INTERNET
        )

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        onNodeWithText(context.getString(R.string.retry)).assertIsDisplayed()
        takeScreenshot()
    }

    @Test
    fun `GIVEN button disabled WHEN screen displayed THEN save button is disabled`() = runTest {
        val uiState = createAddContactUiState(isButtonEnabled = false)

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        onNodeWithText(context.getString(R.string.save_contact)).assertIsNotEnabled()
        takeScreenshot()
    }

    @Test
    fun `GIVEN button enabled WHEN screen displayed THEN save button is enabled`() = runTest {
        val users = createRandomUsers()
        val uiState = createAddContactUiState(
            randomUsers = users.map { Selectable(it, true) },
            isButtonEnabled = true
        )

        setContent {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        onNodeWithText(context.getString(R.string.save_contact)).assertIsEnabled()
        takeScreenshot()
    }

    // endregion

    // region Animation Tests

    @Test
    fun `GIVEN loading state WHEN time advances THEN loading animation progresses`() = runTest {
        val uiState = AddContactUiState(isLoading = true)

        setContent(autoAdvance = false) {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()
    }

    @Test
    fun `GIVEN saving state WHEN time advances THEN saving animation progresses`() = runTest {
        val uiState = createAddContactUiState(isSaving = true)

        setContent(autoAdvance = false) {
            AddContactScreenContent(
                uiState = uiState,
                windowSize = WindowWidthSizeClass.Compact,
                snackbarHostState = SnackbarHostState(),
                onNavigateBack = {},
                onAction = {}
            )
        }

        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()

        advanceTimeBy(500)
        takeScreenshot()
    }

    // endregion

    private fun createRandomUsers() = listOf(
        Contact(
            id = "1",
            name = "Mariia",
            surname = "Kovalenko",
            phone = "+380501111111",
            email = "mariia.kovalenko@gmail.com",
            dateOfBirthday = "10.01.1992",
            avatar = "",
            categories = emptyList()
        ),
        Contact(
            id = "2",
            name = "Andriy",
            surname = "Shevchenko",
            phone = "+380502222222",
            email = "andriy.shevchenko@gmail.com",
            dateOfBirthday = "20.05.1988",
            avatar = "",
            categories = emptyList()
        )
    )

    private fun createCategories() = listOf(
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

    private fun createAddContactUiState(
        isLoading: Boolean = false,
        isLoadingMore: Boolean = false,
        isSaving: Boolean = false,
        isButtonEnabled: Boolean = false,
        randomUsers: List<Selectable<Contact>> = createRandomUsers().map { Selectable(it, false) },
        categories: List<CategoryUiModel> = createCategories(),
        error: NetworkError? = null
    ) = AddContactUiState(
        isLoading = isLoading,
        isLoadingMore = isLoadingMore,
        isSaving = isSaving,
        isButtonEnabled = isButtonEnabled,
        randomUsers = randomUsers,
        categories = categories,
        error = error
    )
}
