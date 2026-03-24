package com.ch000se.profileapp.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.presentation.screens.profile.preview.ProfilePreviewData
import com.ch000se.profileapp.presentation.screens.profile.components.ProfileContent
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    windowSize: WindowWidthSizeClass,
    onNavigateToEdit: () -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { event ->
            when (event) {
                ProfileUiEvent.NavigateToCreateProfile -> onLogout()
            }
        }
    }

    LifecycleStartEffect(key1 = lifecycleOwner) {
        viewModel.onAction(ProfileUiAction.LoadUser)
        onStopOrDispose {}
    }

    ProfileScreenContent(
        user = uiState.user,
        isLoading = uiState.isLoading,
        showLogoutDialog = uiState.showLogoutDialog,
        windowSize = windowSize,
        onEditClick = onNavigateToEdit,
        onBackClick = onNavigateBack,
        onLogoutClick = { viewModel.onAction(ProfileUiAction.ShowLogoutDialog) },
        onConfirmLogout = { viewModel.onAction(ProfileUiAction.ConfirmLogout) },
        onDismissLogout = { viewModel.onAction(ProfileUiAction.DismissLogoutDialog) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreenContent(
    user: User?,
    isLoading: Boolean,
    showLogoutDialog: Boolean,
    windowSize: WindowWidthSizeClass,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onConfirmLogout: () -> Unit,
    onDismissLogout: () -> Unit
) {
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = onDismissLogout,
            icon = {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(stringResource(R.string.logout_confirmation_title)) },
            text = { Text(stringResource(R.string.logout_confirmation_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmLogout) {
                    Text(
                        text = stringResource(R.string.confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissLogout) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onEditClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_profile)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    user?.let {
                        ProfileContent(
                            user = it,
                            windowSize = windowSize
                        )
                    } ?: Text(
                        text = stringResource(R.string.profile_not_found),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    ProfileAppTheme {
        ProfileScreenContent(
            user = ProfilePreviewData.sampleUser,
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
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun ProfileScreenLoadingPreview() {
    ProfileAppTheme {
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
}

@Preview(name = "Logout Dialog", showBackground = true)
@Composable
private fun ProfileScreenLogoutDialogPreview() {
    ProfileAppTheme {
        ProfileScreenContent(
            user = ProfilePreviewData.sampleUser,
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
}

@Preview(name = "Not Found", showBackground = true)
@Composable
private fun ProfileScreenNotFoundPreview() {
    ProfileAppTheme {
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
}