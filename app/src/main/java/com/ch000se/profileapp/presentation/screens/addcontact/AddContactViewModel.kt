package com.ch000se.profileapp.presentation.screens.addcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.domain.mapper.toNetworkError
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.usecases.AddContactUseCase
import com.ch000se.profileapp.domain.usecases.GetRandomUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase,
    private val addContactUseCase: AddContactUseCase,
) : ViewModel(),
    MVI<AddContactUiState, AddContactUiAction, AddContactSideEffect> by mvi(AddContactUiState()) {


    init {
        onStart { onAction(AddContactUiAction.LoadRandomUsers) }
    }

    override fun onAction(action: AddContactUiAction) {
        when (action) {
            AddContactUiAction.LoadRandomUsers -> loadRandomUsers()
            AddContactUiAction.RefreshUsers -> loadRandomUsers()
            AddContactUiAction.LoadMoreUsers -> loadMoreUsers()
            is AddContactUiAction.SelectUser -> selectUser(action.user)
            is AddContactUiAction.ToggleCategory -> toggleCategory(action.category)
            AddContactUiAction.SaveContact -> saveContact()
        }
    }

    private fun loadRandomUsers() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, error = null) }
            getRandomUsersUseCase(PAGE_SIZE)
                .onSuccess { users ->
                    updateUiState { copy(isLoading = false, randomUsers = users) }
                }
                .onFailure { e ->
                    updateUiState { copy(isLoading = false, error = e.toNetworkError()) }
                }
        }
    }


    private fun loadMoreUsers() {
        if (uiState.value.isLoading || uiState.value.isLoadingMore) return

        updateUiState { copy(isLoadingMore = true) }

        viewModelScope.launch {
            getRandomUsersUseCase(PAGE_SIZE)
                .onSuccess { newUsers ->
                    updateUiState {
                        copy(
                            isLoadingMore = false,
                            randomUsers = randomUsers + newUsers
                        )
                    }
                }
                .onFailure {
                    updateUiState { copy(isLoadingMore = false) }
                }
        }
    }

    private fun selectUser(user: Contact) {
        updateUiState { copy(selectedUser = user) }
    }

    private fun toggleCategory(category: ContactCategory) {
        updateUiState {
            val currentCategories = selectedCategories.toMutableList()
            if (category in currentCategories) {
                currentCategories.remove(category)
            } else {
                currentCategories.add(category)
            }
            copy(selectedCategories = currentCategories)
        }
    }

    private fun saveContact() {
        val state = uiState.value
        val selectedUser = state.selectedUser ?: return
        if (state.selectedCategories.isEmpty()) return

        viewModelScope.launch {
            updateUiState { copy(isSaving = true) }
            try {
                val contact = with(selectedUser) {
                    Contact(
                        id = id,
                        name = name,
                        phone = phone,
                        email = email,
                        categories = state.selectedCategories,
                        surname = surname,
                        dateOfBirthday = dateOfBirthday,
                        avatar = avatar
                    )
                }
                addContactUseCase(contact)
                emitSideEffect(AddContactSideEffect.NavigateBack)
            } catch (e: Exception) {
                updateUiState { copy(isSaving = false) }
                emitSideEffect(AddContactSideEffect.ShowError(e.toNetworkError()))
            }
        }
    }
}