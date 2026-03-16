package com.ch000se.profileapp.presentation.screens.contacts

import com.ch000se.profileapp.core.presentation.model.CategoryUiModel
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

data class ContactsUiState(
    val isLoading: Boolean = true,
    val contacts: List<Contact> = emptyList(),
    val query: String = "",
    val error: String? = null,
    val categoryFilters: List<CategoryUiModel> = emptyList()
)

sealed interface ContactsUiAction {
    data object LoadContacts : ContactsUiAction
    data class DeleteContact(val contactId: String) : ContactsUiAction
    data class SearchContacts(val query: String) : ContactsUiAction
    data class ToggleCategoryFilter(val category: ContactCategory) : ContactsUiAction
}