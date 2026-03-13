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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase
) : ViewModel(),
    MVI<ContactsUiState, ContactsUiAction, ContactsSideEffect> by mvi(ContactsUiState()) {
    private var loadContactsJob: Job? = null

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
        if (loadContactsJob?.isActive == true) return

        loadContactsJob = viewModelScope.launch {
            getContactsUseCase()
                .catch { e -> updateUiState { copy(isLoading = false, error = e.message) } }
                .collect { contacts ->
                    updateUiState { copy(contacts = contacts, isLoading = false, error = null) }
                }
        }
    }

    private fun deleteContact(contactId: String) {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }
            try {
                deleteContactUseCase(contactId)
            } catch (e: Exception) {
                updateUiState { copy(error = e.message) }
            } finally {
                updateUiState { copy(isLoading = false) }
            }
        }
    }
}