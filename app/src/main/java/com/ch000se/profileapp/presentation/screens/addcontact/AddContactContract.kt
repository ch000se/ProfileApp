package com.ch000se.profileapp.presentation.screens.addcontact

import com.ch000se.profileapp.core.error.NetworkError
import com.ch000se.profileapp.presentation.common.model.CategoryUiModel
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

data class Selectable<T>(
    val data: T,
    val isSelected: Boolean = false
)

data class AddContactUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isSaving: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val randomUsers: List<Selectable<Contact>> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),
    val error: NetworkError? = null
)

sealed interface AddContactUiAction {
    data object LoadRandomUsers : AddContactUiAction
    data object LoadMoreUsers : AddContactUiAction
    data object RefreshUsers : AddContactUiAction
    data class SelectUser(val user: Contact) : AddContactUiAction
    data class ToggleCategory(val category: ContactCategory) : AddContactUiAction
    data object SaveContact : AddContactUiAction
}

sealed interface AddContactSideEffect {
    data object NavigateBack : AddContactSideEffect
    data class ShowError(val message: NetworkError) : AddContactSideEffect
}