package com.ch000se.profileapp.presentation.screens.contactDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.mvi.MVI
import com.ch000se.profileapp.core.mvi.mvi
import com.ch000se.profileapp.domain.usecases.GetContactByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ContactDetailViewModel.Factory::class)
class ContactDetailViewModel @AssistedInject constructor(
    private val getContactByIdUseCase: GetContactByIdUseCase,
    @Assisted("contactId") private val contactId: String
) : ViewModel(), MVI<ContactDetailUiState, ContactDetailUiAction, Nothing> by mvi(
    ContactDetailUiState()
) {
    init {
        onAction(ContactDetailUiAction.LoadUser)
    }

    override fun onAction(action: ContactDetailUiAction) {
        when (action) {
            ContactDetailUiAction.LoadUser -> loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, error = null) }
            try {
                val user = getContactByIdUseCase(contactId)
                updateUiState { copy(isLoading = false, contact = user) }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("contactId") contactId: String): ContactDetailViewModel
    }
}