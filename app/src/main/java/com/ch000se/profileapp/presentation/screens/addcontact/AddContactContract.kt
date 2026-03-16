package com.ch000se.profileapp.presentation.screens.addcontact

import android.content.Context
import com.ch000se.profileapp.core.domain.mapper.NetworkError
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

data class Selectable<T>(
    val data: T,
    val isSelected: Boolean = false
)

sealed interface UiText {
    data class StringResource(val resId: Int) : UiText
    data class DynamicString(val value: String) : UiText

    fun asString(context: Context): String = when (this) {
        is StringResource -> context.getString(resId)
        is DynamicString -> value
    }
}

data class CategoryUiModel(
    val category: ContactCategory,
    val label: UiText,
    val isSelected: Boolean = false
)

data class AddContactUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isSaving: Boolean = false,
    val randomUsers: List<Selectable<Contact>> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),
    val error: NetworkError? = null
) {
    val selectedUser: Contact?
        get() = randomUsers.firstOrNull { it.isSelected }?.data
    val selectedCategories: List<ContactCategory>
        get() = categories.filter { it.isSelected }.map { it.category }

}

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