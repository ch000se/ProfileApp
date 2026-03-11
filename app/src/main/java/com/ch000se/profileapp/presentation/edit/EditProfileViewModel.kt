package com.ch000se.profileapp.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.mvi
import com.ch000se.profileapp.core.presentation.mvi.onStart
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.usecases.GetUserUseCase
import com.ch000se.profileapp.domain.usecases.SaveUserUseCase
import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.UserValidator
import com.ch000se.profileapp.domain.validation.ValidationError
import com.ch000se.profileapp.domain.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val userValidator: UserValidator
) : ViewModel(),
    MVI<EditProfileUiState, EditProfileUiAction, EditProfileSideEffect> by mvi(EditProfileUiState()) {

    init {
        onStart { loadUser() }
    }

    override fun onAction(action: EditProfileUiAction) {
        when (action) {
            EditProfileUiAction.LoadUser -> loadUser()
            is EditProfileUiAction.UpdateName -> {
                updateUiState {
                    copy(
                        name = action.value,
                        validationErrors = updateValidationError(
                            UserField.NAME,
                            userValidator.validateName(action.value)
                        )
                    )
                }
            }

            is EditProfileUiAction.UpdateSurname -> {
                updateUiState {
                    copy(
                        surname = action.value,
                        validationErrors = updateValidationError(
                            UserField.SURNAME,
                            userValidator.validateSurname(action.value)
                        )
                    )
                }
            }

            is EditProfileUiAction.UpdatePhone -> {
                updateUiState {
                    copy(
                        phone = action.value,
                        validationErrors = updateValidationError(
                            UserField.PHONE,
                            userValidator.validatePhone(action.value)
                        )
                    )
                }
            }

            is EditProfileUiAction.UpdateEmail -> {
                updateUiState {
                    copy(
                        email = action.value,
                        validationErrors = updateValidationError(
                            UserField.EMAIL,
                            userValidator.validateEmail(action.value)
                        )
                    )
                }
            }

            is EditProfileUiAction.UpdateDateOfBirthday -> {
                updateUiState {
                    copy(
                        dateOfBirthday = action.value,
                        validationErrors = updateValidationError(
                            UserField.DATE_OF_BIRTHDAY,
                            userValidator.validateDateOfBirthday(action.value)
                        )
                    )
                }
            }

            is EditProfileUiAction.UpdateAvatar -> updateUiState { copy(avatarUri = action.uri) }
            EditProfileUiAction.SaveProfile -> saveProfile()
            EditProfileUiAction.ShowDatePicker -> updateUiState { copy(showDatePicker = true) }
            EditProfileUiAction.HideDatePicker -> updateUiState { copy(showDatePicker = false) }
            EditProfileUiAction.ShowImagePicker -> viewModelScope.emitSideEffect(
                EditProfileSideEffect.OpenImagePicker
            )
        }
    }

    private fun EditProfileUiState.updateValidationError(
        field: UserField,
        error: ValidationError?
    ): Map<UserField, ValidationError> {
        return if (error != null) {
            validationErrors + (field to error)
        } else {
            validationErrors - field
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = getUserUseCase().firstOrNull()
            user?.let {
                updateUiState {
                    copy(
                        name = it.name,
                        surname = it.surname,
                        phone = it.phone,
                        email = it.email,
                        dateOfBirthday = it.dateOfBirthday,
                        avatarUri = it.avatar
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true, validationErrors = emptyMap()) }

            val user = with(uiState.value) {
                User(
                    name = name,
                    surname = surname,
                    phone = phone,
                    email = email,
                    dateOfBirthday = dateOfBirthday,
                    avatar = avatarUri
                )
            }

            when (val result = saveUserUseCase(user)) {
                is ValidationResult.Success -> {
                    updateUiState { copy(isLoading = false, isSaved = true) }
                    viewModelScope.emitSideEffect(EditProfileSideEffect.NavigateBack)
                }

                is ValidationResult.Error -> {
                    updateUiState { copy(isLoading = false, validationErrors = result.errors) }
                }
            }
        }
    }
}