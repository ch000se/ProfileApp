package com.ch000se.profileapp.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.coroutines.AppDispatchers
import com.ch000se.profileapp.core.mvi.MVI
import com.ch000se.profileapp.core.mvi.mvi
import com.ch000se.profileapp.core_ui.model.UiText
import com.ch000se.profileapp.core_ui.mvi.onStart
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.DeleteContactUseCase
import com.ch000se.profileapp.domain.usecases.SearchContactsUseCase
import com.ch000se.profileapp.presentation.common.model.CategoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val searchContactsUseCase: SearchContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel(),
    MVI<ContactsUiState, ContactsUiAction, Nothing> by mvi(ContactsUiState(categoryFilters = initialCategories)) {

    companion object {
        private val initialCategories = ContactCategory.entries.map { category ->
            CategoryUiModel(
                category = category,
                label = UiText.StringResource(
                    when (category) {
                        ContactCategory.FAMILY -> R.string.category_family
                        ContactCategory.FRIENDS -> R.string.category_friends
                        ContactCategory.WORK -> R.string.category_work
                    }
                ),
                isSelected = true
            )
        }
    }


    private val query = MutableStateFlow("")

    init {
        onStart {
            query
                .onEach { input ->
                    updateUiState { copy(query = input) }
                }
                .debounce(300L)
                .onEach { input -> fetchContacts(input) }
                .launchIn(viewModelScope)
        }
    }

    override fun onAction(action: ContactsUiAction) {
        when (action) {
            is ContactsUiAction.DeleteContact -> deleteContact(action.contactId)
            ContactsUiAction.LoadContacts -> loadContacts()
            is ContactsUiAction.SearchContacts -> query.update { action.query }
            is ContactsUiAction.ToggleCategoryFilter -> toggleCategoryFilter(action.category)
        }
    }

    private fun loadContacts() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            fetchContacts(query.value)
        }
    }

    private suspend fun fetchContacts(input: String) {
        try {
            val selectedCategories = uiState.value.categoryFilters
                .filter { it.isSelected }
                .map { it.category }

            if (selectedCategories.isEmpty()) {
                updateUiState { copy(contacts = emptyList(), error = null) }
                return
            }

            val contacts = searchContactsUseCase(input.trim(), selectedCategories)

            updateUiState {
                copy(
                    contacts = contacts,
                    error = null
                )
            }
        } catch (e: Exception) {
            updateUiState { copy(error = e.message) }
        }
    }

    private fun toggleCategoryFilter(category: ContactCategory) {
        updateUiState {
            val updatedFilters = categoryFilters.map {
                if (it.category == category) it.copy(isSelected = !it.isSelected) else it
            }
            copy(categoryFilters = updatedFilters)
        }

        viewModelScope.launch(dispatchers.mainImmediate) { fetchContacts(query.value) }
    }


    private fun deleteContact(contactId: String) {
        viewModelScope.launch(dispatchers.mainImmediate) {
            updateUiState { copy(isDeleting = true) }
            try {
                deleteContactUseCase(contactId)
                updateUiState {
                    copy(contacts = contacts.filter { it.id != contactId })
                }
            } catch (e: Exception) {
                updateUiState { copy(error = e.message) }
            } finally {
                updateUiState { copy(isDeleting = false) }
            }
        }
    }
}