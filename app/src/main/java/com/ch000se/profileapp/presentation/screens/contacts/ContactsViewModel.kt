package com.ch000se.profileapp.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.domain.usecases.DeleteContactUseCase
import com.ch000se.profileapp.domain.usecases.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase
) : ViewModel(),
    MVI<ContactsUiState, ContactsUiAction, Nothing> by mvi(ContactsUiState()) {

    override fun onAction(action: ContactsUiAction) {
        when (action) {
            is ContactsUiAction.DeleteContact -> deleteContact(action.contactId)
            ContactsUiAction.LoadContacts -> loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }
            try {
                val contacts = getContactsUseCase()
                updateUiState { copy(contacts = contacts, isLoading = false, error = null) }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun deleteContact(contactId: String) {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }
            try {
                deleteContactUseCase(contactId)
                onAction(ContactsUiAction.LoadContacts)
            } catch (e: Exception) {
                updateUiState { copy(error = e.message) }
            } finally {
                updateUiState { copy(isLoading = false) }
            }
        }
    }
}