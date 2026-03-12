package com.ch000se.profileapp.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.emitSideEffect
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.usecases.DeleteContactUseCase
import com.ch000se.profileapp.domain.usecases.GetContactsUseCase
import com.ch000se.profileapp.presentation.screens.contacts.ContactsSideEffect.NavigateToContactDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase
) : ViewModel(),
    MVI<ContactsUiState, ContactsUiAction, ContactsSideEffect> by mvi(ContactsUiState()) {

    init {
        onStart { onAction(ContactsUiAction.LoadContacts) }
    }

    override fun onAction(action: ContactsUiAction) {
        when (action) {
            ContactsUiAction.AddContact -> emitSideEffect(ContactsSideEffect.NavigateToAddContact)
            is ContactsUiAction.DeleteContact -> deleteContact(action.contactId)
            is ContactsUiAction.OpenContactDetail -> emitSideEffect(
                NavigateToContactDetail(
                    action.contact
                )
            )
            ContactsUiAction.LoadContacts -> loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            getContactsUseCase()
                .collect { contacts ->
                    updateUiState { copy(contacts = contacts, isLoading = false, error = null) }
                }
        }
    }

    private fun deleteContact(contactId: String) {
        viewModelScope.launch {
            try {
                deleteContactUseCase(contactId)
            } catch (e: Exception) {
                updateUiState { copy(error = e.message) }
            }
        }
    }
}