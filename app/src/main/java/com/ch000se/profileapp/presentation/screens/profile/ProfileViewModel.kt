package com.ch000se.profileapp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), MVI<ProfileUiState, ProfileUiAction, Nothing> by mvi(ProfileUiState()) {

    override fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.LoadUser -> loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, error = null) }
            try {
                val user = getUserUseCase()
                updateUiState { copy(isLoading = false, user = user) }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}