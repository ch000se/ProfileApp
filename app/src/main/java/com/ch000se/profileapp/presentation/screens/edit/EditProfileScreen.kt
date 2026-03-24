package com.ch000se.profileapp.presentation.screens.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.R
import com.ch000se.profileapp.presentation.screens.edit.preview.EditProfilePreviewData
import com.ch000se.profileapp.presentation.screens.edit.components.BirthDatePickerDialog
import com.ch000se.profileapp.presentation.screens.edit.components.EditProfileContentCompact
import com.ch000se.profileapp.presentation.screens.edit.components.EditProfileContentExpanded
import com.ch000se.profileapp.ui.theme.ProfileAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    windowSize: WindowWidthSizeClass,
    isCreateMode: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onAction(EditProfileUiAction.UpdateAvatar(it.toString()))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                EditProfileSideEffect.NavigateBack -> onNavigateBack()
                EditProfileSideEffect.OpenImagePicker -> {
                    imagePickerLauncher.launch("image/*")
                }
            }
        }
    }

    if (uiState.showDatePicker) {
        BirthDatePickerDialog(
            onDateSelected = { dateMillis ->
                dateMillis?.let {
                    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
                    viewModel.onAction(EditProfileUiAction.UpdateDateOfBirthday(date))
                }
                viewModel.onAction(EditProfileUiAction.HideDatePicker)
            },
            onDismiss = {
                viewModel.onAction(EditProfileUiAction.HideDatePicker)
            }
        )
    }

    EditProfileScreenContent(
        uiState = uiState,
        isCreateMode = isCreateMode,
        windowSize = windowSize,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileScreenContent(
    uiState: EditProfileUiState,
    isCreateMode: Boolean,
    windowSize: WindowWidthSizeClass,
    onAction: (EditProfileUiAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isCreateMode) R.string.create_profile else R.string.edit_profile
                        )
                    )
                },
                navigationIcon = {
                    if (!isCreateMode) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                EditProfileContentCompact(
                    uiState = uiState,
                    isCreateMode = isCreateMode,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                EditProfileContentExpanded(
                    uiState = uiState,
                    isCreateMode = isCreateMode,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditProfileScreenPreview() {
    ProfileAppTheme {
        EditProfileScreenContent(
            uiState = EditProfilePreviewData.sampleEditProfileUiState,
            isCreateMode = false,
            windowSize = WindowWidthSizeClass.Compact,
            onAction = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Create Mode", showBackground = true)
@Composable
private fun EditProfileScreenCreateModePreview() {
    ProfileAppTheme {
        EditProfileScreenContent(
            uiState = EditProfileUiState(),
            isCreateMode = true,
            windowSize = WindowWidthSizeClass.Compact,
            onAction = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun EditProfileScreenLoadingPreview() {
    ProfileAppTheme {
        EditProfileScreenContent(
            uiState = EditProfilePreviewData.sampleEditProfileUiStateLoading,
            isCreateMode = false,
            windowSize = WindowWidthSizeClass.Compact,
            onAction = {},
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Validation Errors", showBackground = true)
@Composable
private fun EditProfileScreenValidationErrorsPreview() {
    ProfileAppTheme {
        EditProfileScreenContent(
            uiState = EditProfilePreviewData.sampleEditProfileUiStateWithErrors,
            isCreateMode = false,
            windowSize = WindowWidthSizeClass.Compact,
            onAction = {},
            onNavigateBack = {}
        )
    }
}
