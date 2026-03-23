package com.ch000se.profileapp.presentation.screens.addcontact.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core_ui.components.PaginatedLazyColumn
import com.ch000se.profileapp.presentation.preview.PreviewData
import com.ch000se.profileapp.presentation.screens.addcontact.AddContactUiAction
import com.ch000se.profileapp.presentation.screens.addcontact.AddContactUiState
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@Composable
fun AddContactContent(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    onAction: (AddContactUiAction) -> Unit,
    uiState: AddContactUiState,
) {
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            AddContactContentCompact(
                onAction = onAction,
                uiState = uiState,
                modifier = modifier
            )
        }

        else -> {
            AddContentExpanded(
                onAction = onAction,
                uiState = uiState,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AddContactContentCompact(
    modifier: Modifier = Modifier,
    onAction: (AddContactUiAction) -> Unit,
    uiState: AddContactUiState,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.select_user),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        PaginatedLazyColumn(
            items = uiState.randomUsers,
            key = { it.data.id },
            isLoadingMore = uiState.isLoadingMore,
            onLoadMore = { onAction(AddContactUiAction.LoadMoreUsers) },
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { _, selectable ->
            val user = selectable.data
            RandomUserItem(
                user = user,
                isSelected = selectable.isSelected,
                onClick = {
                    onAction(AddContactUiAction.SelectUser(user))
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CategorySelector(
                onCategoryToggle = { category ->
                    onAction(AddContactUiAction.ToggleCategory(category))
                },
                categories = uiState.categories
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onAction(AddContactUiAction.SaveContact) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isButtonEnabled
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.save_contact))
                }
            }
        }
    }
}


@Composable
fun AddContentExpanded(
    modifier: Modifier = Modifier,
    onAction: (AddContactUiAction) -> Unit,
    uiState: AddContactUiState,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.select_user),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PaginatedLazyColumn(
                items = uiState.randomUsers,
                key = { it.data.id },
                isLoadingMore = uiState.isLoadingMore,
                onLoadMore = { onAction(AddContactUiAction.LoadMoreUsers) },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { _, selectable ->
                val user = selectable.data
                RandomUserItem(
                    user = user,
                    isSelected = selectable.isSelected,
                    onClick = {
                        onAction(AddContactUiAction.SelectUser(user))
                    }
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CategorySelector(
                categories = uiState.categories,
                onCategoryToggle = {
                    onAction(AddContactUiAction.ToggleCategory(it))
                }
            )

            Button(
                onClick = { onAction(AddContactUiAction.SaveContact) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isButtonEnabled
            ) {
                Text(stringResource(R.string.save_contact))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AddContactContentCompactPreview() {
    ProfileAppTheme {
        Surface {
            AddContactContentCompact(
                uiState = PreviewData.sampleAddContactUiStateWithSelection,
                onAction = {}
            )
        }
    }
}

@Preview(name = "Expanded", showBackground = true, widthDp = 840, heightDp = 480)
@Composable
private fun AddContactContentExpandedPreview() {
    ProfileAppTheme {
        Surface {
            AddContentExpanded(
                uiState = PreviewData.sampleAddContactUiStateWithSelection,
                onAction = {}
            )
        }
    }
}