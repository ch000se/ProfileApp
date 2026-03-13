package com.ch000se.profileapp.presentation.screens.contactDetail

import com.ch000se.profileapp.domain.model.Contact

data class ContactDetailUiState(
    val isLoading: Boolean = true,
    val contact: Contact? = null,
    val error: String? = null
)

sealed interface ContactDetailUiAction {
    data object LoadUser : ContactDetailUiAction
}

sealed interface ContactDetailSideEffect {
    data object NavigateToContactScreen : ContactDetailSideEffect
}