package com.ch000se.profileapp.presentation.screens.contacts

import com.ch000se.profileapp.domain.model.Contact

data class ContactsUiState(
    val isLoading: Boolean = true,
    val contacts: List<Contact> = emptyList(),
    val error: String? = null
)

sealed interface ContactsUiAction {
    data object LoadContacts : ContactsUiAction
    data class DeleteContact(val contactId: String) : ContactsUiAction
}