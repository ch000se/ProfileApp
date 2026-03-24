package com.ch000se.profileapp.presentation.screens.contactDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ch000se.profileapp.R
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.presentation.screens.contactDetail.preview.ContactDetailPreviewData
import com.ch000se.profileapp.presentation.screens.contactDetail.component.ContactDetailContent
import com.ch000se.profileapp.ui.theme.ProfileAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    windowSize: WindowWidthSizeClass,
    contactId: String,
    viewModel: ContactDetailViewModel = hiltViewModel(
        creationCallback = { factory: ContactDetailViewModel.Factory ->
            factory.create(contactId)
        }
    ),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactDetailScreenContent(
        contact = uiState.contact,
        isLoading = uiState.isLoading,
        windowSize = windowSize,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContactDetailScreenContent(
    contact: Contact?,
    isLoading: Boolean,
    windowSize: WindowWidthSizeClass,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
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
                    contact?.let {
                        ContactDetailContent(
                            contact = it,
                            windowSize = windowSize,
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
private fun ContactDetailScreenPreview() {
    ProfileAppTheme {
        ContactDetailScreenContent(
            contact = ContactDetailPreviewData.sampleContact,
            isLoading = false,
            windowSize = WindowWidthSizeClass.Compact,
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun ContactDetailScreenLoadingPreview() {
    ProfileAppTheme {
        ContactDetailScreenContent(
            contact = null,
            isLoading = true,
            windowSize = WindowWidthSizeClass.Compact,
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Not Found", showBackground = true)
@Composable
private fun ContactDetailScreenNotFoundPreview() {
    ProfileAppTheme {
        ContactDetailScreenContent(
            contact = null,
            isLoading = false,
            windowSize = WindowWidthSizeClass.Compact,
            onNavigateBack = {}
        )
    }
}

