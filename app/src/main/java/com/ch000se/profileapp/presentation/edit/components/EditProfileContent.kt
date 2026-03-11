package com.ch000se.profileapp.presentation.edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ch000se.profileapp.presentation.edit.EditProfileUiAction
import com.ch000se.profileapp.presentation.edit.EditProfileUiState

@Composable
fun EditProfileContentCompact(
    uiState: EditProfileUiState,
    isCreateMode: Boolean,
    onAction: (EditProfileUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarSection(
            avatarUri = uiState.avatarUri,
            onAvatarClick = { onAction(EditProfileUiAction.ShowImagePicker) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormFields(
            uiState = uiState,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(24.dp))

        SaveButton(
            isLoading = uiState.isLoading,
            isEnabled = uiState.isValid,
            isCreateMode = isCreateMode,
            onClick = { onAction(EditProfileUiAction.SaveProfile) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EditProfileContentExpanded(
    uiState: EditProfileUiState,
    isCreateMode: Boolean,
    onAction: (EditProfileUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AvatarSection(
                avatarUri = uiState.avatarUri,
                onAvatarClick = { onAction(EditProfileUiAction.ShowImagePicker) },
                avatarSize = 160.dp
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            FormFields(
                uiState = uiState,
                onAction = onAction
            )

            Spacer(modifier = Modifier.height(24.dp))

            SaveButton(
                isLoading = uiState.isLoading,
                isEnabled = uiState.isValid,
                isCreateMode = isCreateMode,
                onClick = { onAction(EditProfileUiAction.SaveProfile) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}