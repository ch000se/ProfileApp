package com.ch000se.profileapp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.coroutines.AppDispatchers
import com.ch000se.profileapp.core.mvi.MVI
import com.ch000se.profileapp.core.mvi.mvi
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import com.ch000se.profileapp.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel(), MVI<ProfileUiState, ProfileUiAction, ProfileUiEvent> by mvi(ProfileUiState()) {

    override fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.LoadUser -> loadUser()
            ProfileUiAction.ShowLogoutDialog -> updateUiState { copy(showLogoutDialog = true) }
            ProfileUiAction.DismissLogoutDialog -> updateUiState { copy(showLogoutDialog = false) }
            ProfileUiAction.ConfirmLogout -> logout()
        }
    }

    private fun loadUser() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            updateUiState { copy(isLoading = true, error = null) }
            try {
                val user = getUserUseCase()
                updateUiState { copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            updateUiState { copy(isLoading = true) }
            try {
                logoutUseCase()
                emitSideEffect(ProfileUiEvent.NavigateToCreateProfile)
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}