package com.ch000se.profileapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.domain.usecases.IsUserExistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Ready(val isUserExist: Boolean) : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isUserExistUseCase: IsUserExistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkUser()
    }

    private fun checkUser() {
        viewModelScope.launch {
            val exists = isUserExistUseCase()
            _uiState.value = MainUiState.Ready(isUserExist = exists)
        }
    }
}