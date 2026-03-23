package com.ch000se.profileapp.presentation.screens.addcontact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.error.NetworkError
import com.ch000se.profileapp.presentation.common.mapper.asString
import com.ch000se.profileapp.presentation.screens.addcontact.preview.AddContactPreviewData
import com.ch000se.profileapp.presentation.screens.addcontact.components.AddContactContent
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    windowSize: WindowWidthSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: AddContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                AddContactSideEffect.NavigateBack -> onNavigateBack()
                is AddContactSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    AddContactScreenContent(
        uiState = uiState,
        windowSize = windowSize,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContactScreenContent(
    uiState: AddContactUiState,
    windowSize: WindowWidthSizeClass,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onAction: (AddContactUiAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_contact_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onAction(AddContactUiAction.RefreshUsers) },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error?.asString() ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onAction(AddContactUiAction.RefreshUsers) }
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }

                else -> {
                    AddContactContent(
                        windowSize = windowSize,
                        onAction = onAction,
                        uiState = uiState
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AddContactScreenPreview() {
    ProfileAppTheme {
        AddContactScreenContent(
            uiState = AddContactPreviewData.sampleAddContactUiStateWithSelection,
            windowSize = WindowWidthSizeClass.Compact,
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onAction = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun AddContactScreenLoadingPreview() {
    ProfileAppTheme {
        AddContactScreenContent(
            uiState = AddContactUiState(isLoading = true),
            windowSize = WindowWidthSizeClass.Compact,
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onAction = {}
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun AddContactScreenErrorPreview() {
    ProfileAppTheme {
        AddContactScreenContent(
            uiState = AddContactUiState(
                isLoading = false,
                error = NetworkError.NO_INTERNET
            ),
            windowSize = WindowWidthSizeClass.Compact,
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onAction = {}
        )
    }
}

@Preview(name = "Saving", showBackground = true)
@Composable
private fun AddContactScreenSavingPreview() {
    ProfileAppTheme {
        AddContactScreenContent(
            uiState = AddContactPreviewData.sampleAddContactUiStateWithSelection.copy(
                isSaving = true,
                isButtonEnabled = false
            ),
            windowSize = WindowWidthSizeClass.Compact,
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onAction = {}
        )
    }
}