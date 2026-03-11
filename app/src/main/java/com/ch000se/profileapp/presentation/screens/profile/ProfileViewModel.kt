package com.ch000se.profileapp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.emitSideEffect
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), MVI<ProfileUiState, ProfileUiAction, ProfileSideEffect> by mvi(ProfileUiState()) {

    init {
        onStart { onAction(ProfileUiAction.LoadUser) }
    }

    override fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.LoadUser -> loadUser()
            ProfileUiAction.EditProfile -> emitSideEffect(ProfileSideEffect.NavigateToEditProfile)
            ProfileUiAction.CreateProfile -> emitSideEffect(ProfileSideEffect.NavigateToCreateProfile)
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, error = null) }
            try {
                val user = getUserUseCase()
                if (user != null) {
                    updateUiState { copy(isLoading = false, user = user) }
                } else {
                    onAction(ProfileUiAction.CreateProfile)
                }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}