package com.ch000se.profileapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), MVI<ProfileUiState, ProfileUiAction, ProfileSideEffect> by mvi(ProfileUiState()) {

    init {
        onStart { loadUser() }
    }

    override fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.LoadUser -> loadUser()
            ProfileUiAction.EditProfile -> viewModelScope.emitSideEffect(ProfileSideEffect.NavigateToEditProfile)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, error = null) }
            getUserUseCase()
                .catch { e -> updateUiState { copy(isLoading = false, error = e.message) } }
                .collect { user ->
                    updateUiState { copy(isLoading = false, user = user) }
                }
        }
    }
}