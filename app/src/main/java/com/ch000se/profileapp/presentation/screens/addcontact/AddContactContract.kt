package com.ch000se.profileapp.presentation.screens.addcontact

import com.ch000se.profileapp.core.domain.mapper.NetworkError
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

data class AddContactUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val randomUsers: List<Contact> = emptyList(),
    val selectedUser: Contact? = null,
    val selectedCategories: List<ContactCategory> = emptyList(),
    val error: NetworkError? = null
)

sealed interface AddContactUiAction {
    data object LoadRandomUsers : AddContactUiAction
    data object RefreshUsers : AddContactUiAction
    data class SelectUser(val user: Contact) : AddContactUiAction
    data class ToggleCategory(val category: ContactCategory) : AddContactUiAction
    data object SaveContact : AddContactUiAction
}

sealed interface AddContactSideEffect {
    data object NavigateBack : AddContactSideEffect
    data class ShowError(val message: NetworkError) : AddContactSideEffect
}