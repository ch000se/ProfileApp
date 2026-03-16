package com.ch000se.profileapp.presentation.screens.profile

import com.ch000se.profileapp.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)

sealed interface ProfileUiAction {
    data object LoadUser : ProfileUiAction
}