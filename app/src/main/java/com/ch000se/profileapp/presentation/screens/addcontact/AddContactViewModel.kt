package com.ch000se.profileapp.presentation.screens.addcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.R
import com.ch000se.profileapp.core.domain.mapper.toNetworkError
import com.ch000se.profileapp.core.presentation.model.CategoryUiModel
import com.ch000se.profileapp.core.presentation.model.UiText
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
    MVI<AddContactUiState, AddContactUiAction, AddContactSideEffect> by mvi(
        AddContactUiState(categories = initialCategories)
    ) {

    companion object {
        private val initialCategories = ContactCategory.entries.map {
            CategoryUiModel(
                category = it,
                label = UiText.StringResource(
                    when (it) {
                        ContactCategory.FAMILY -> R.string.category_family
                        ContactCategory.FRIENDS -> R.string.category_friends
                        ContactCategory.WORK -> R.string.category_work
                    }
                )
            )
        }
    }

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
            updateUiState { copy(isLoading = true, error = null, randomUsers = emptyList()) }
            getRandomUsersUseCase(PAGE_SIZE)
                .onSuccess { users ->
                    updateUiState {
                        copy(
                            isLoading = false,
                            randomUsers = users.map { Selectable(data = it) })
                    }
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
                            randomUsers = randomUsers + newUsers.map { Selectable(data = it) }
                        )
                    }
                }
                .onFailure {
                    updateUiState { copy(isLoadingMore = false) }
                }
        }
    }


    private fun isButtonEnabled(state: AddContactUiState): Boolean {
        val hasSelectedUser = state.randomUsers.any { it.isSelected }
        val hasSelectedCategory = state.categories.any { it.isSelected }

        return hasSelectedUser && hasSelectedCategory && !state.isSaving
    }

    private fun selectUser(user: Contact) {
        updateUiState {
            val newUsers = randomUsers.map {
                it.copy(isSelected = it.data == user)
            }

            val newState = copy(randomUsers = newUsers)

            newState.copy(
                isButtonEnabled = isButtonEnabled(newState)
            )
        }
    }

    private fun toggleCategory(category: ContactCategory) {
        updateUiState {
            val newCategories = categories.map {
                if (it.category == category)
                    it.copy(isSelected = !it.isSelected)
                else it
            }

            val newState = copy(categories = newCategories)

            newState.copy(
                isButtonEnabled = isButtonEnabled(newState)
            )
        }
    }

    private fun saveContact() {
        val state = uiState.value

        val selectedUser = state.randomUsers
            .firstOrNull { it.isSelected }?.data ?: return

        val selectedCategories = state.categories
            .filter { it.isSelected }
            .map { it.category }

        if (selectedCategories.isEmpty()) return

        viewModelScope.launch {
            updateUiState { copy(isSaving = true, isButtonEnabled = false) }

            try {
                val contact = selectedUser.copy(categories = selectedCategories)
                addContactUseCase(contact)

                updateUiState { copy(isSaving = false) }

                emitSideEffect(AddContactSideEffect.NavigateBack)
            } catch (e: Exception) {
                updateUiState {
                    val newState = copy(isSaving = false)
                    newState.copy(isButtonEnabled = isButtonEnabled(newState))
                }

                emitSideEffect(AddContactSideEffect.ShowError(e.toNetworkError()))
            }
        }
    }
}