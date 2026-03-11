package com.ch000se.profileapp.presentation.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch000se.profileapp.core.presentation.mvi.MVI
import com.ch000se.profileapp.core.presentation.mvi.emitSideEffect
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
        onStart { onAction(EditProfileUiAction.LoadUser) }
    }

    override fun onAction(action: EditProfileUiAction) {
        when (action) {
            EditProfileUiAction.LoadUser -> loadUser()
            is EditProfileUiAction.UpdateName -> updateUiState {
                copy(
                    name = action.value,
                    validationErrors = updateValidationError(
                        UserField.NAME,
                        userValidator.validateName(action.value)
                    )
                ).withUpdatedSaveButton()
            }

            is EditProfileUiAction.UpdateSurname -> updateUiState {
                copy(
                    surname = action.value,
                    validationErrors = updateValidationError(
                        UserField.SURNAME,
                        userValidator.validateSurname(action.value)
                    )
                ).withUpdatedSaveButton()
            }

            is EditProfileUiAction.UpdatePhone -> updateUiState {
                copy(
                    phone = action.value,
                    validationErrors = updateValidationError(
                        UserField.PHONE,
                        userValidator.validatePhone(action.value)
                    )
                ).withUpdatedSaveButton()
            }

            is EditProfileUiAction.UpdateEmail -> updateUiState {
                copy(
                    email = action.value,
                    validationErrors = updateValidationError(
                        UserField.EMAIL,
                        userValidator.validateEmail(action.value)
                    )
                ).withUpdatedSaveButton()
            }

            is EditProfileUiAction.UpdateDateOfBirthday -> updateUiState {
                copy(
                    dateOfBirthday = action.value,
                    validationErrors = updateValidationError(
                        UserField.DATE_OF_BIRTHDAY,
                        userValidator.validateDateOfBirthday(action.value)
                    )
                ).withUpdatedSaveButton()
            }

            is EditProfileUiAction.UpdateAvatar -> updateUiState { copy(avatarUri = action.uri) }
            EditProfileUiAction.SaveProfile -> saveProfile()
            EditProfileUiAction.ShowDatePicker -> updateUiState { copy(showDatePicker = true) }
            EditProfileUiAction.HideDatePicker -> updateUiState { copy(showDatePicker = false) }
            EditProfileUiAction.ShowImagePicker -> emitSideEffect(
                EditProfileSideEffect.OpenImagePicker
            )

            EditProfileUiAction.ProfileScreen -> emitSideEffect(EditProfileSideEffect.NavigateBack)
        }
    }

    private fun EditProfileUiState.withUpdatedSaveButton(): EditProfileUiState {
        val isEnabled = validationErrors.isEmpty() &&
                name.isNotBlank() &&
                surname.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                dateOfBirthday.isNotBlank()
        return copy(isSaveButtonEnabled = isEnabled)
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
            getUserUseCase()?.let { user ->
                updateUiState {
                    copy(
                        name = user.name,
                        surname = user.surname,
                        phone = user.phone,
                        email = user.email,
                        dateOfBirthday = user.dateOfBirthday,
                        avatarUri = user.avatar
                    ).withUpdatedSaveButton()
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
                    updateUiState { copy(isLoading = false) }
                    onAction(EditProfileUiAction.ProfileScreen)
                }

                is ValidationResult.Error -> {
                    updateUiState {
                        copy(
                            isLoading = false,
                            validationErrors = result.errors
                        ).withUpdatedSaveButton()
                    }
                }
            }
        }
    }
}