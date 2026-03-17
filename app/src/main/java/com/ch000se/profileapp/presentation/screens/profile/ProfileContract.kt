package com.ch000se.profileapp.presentation.screens.profile

import com.ch000se.profileapp.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
    val showLogoutDialog: Boolean = false
)

sealed interface ProfileUiAction {
    data object LoadUser : ProfileUiAction
    data object ShowLogoutDialog : ProfileUiAction
    data object DismissLogoutDialog : ProfileUiAction
    data object ConfirmLogout : ProfileUiAction
}

sealed interface ProfileUiEvent {
    data object NavigateToCreateProfile : ProfileUiEvent
}