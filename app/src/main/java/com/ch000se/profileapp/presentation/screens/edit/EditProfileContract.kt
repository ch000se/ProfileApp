package com.ch000se.profileapp.presentation.screens.edit

import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.ValidationError

data class EditProfileUiState(
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val dateOfBirthday: String = "",
    val avatarUri: String = "",
    val validationErrors: Map<UserField, ValidationError> = emptyMap(),
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val isSaveButtonEnabled: Boolean = false
)

sealed interface EditProfileUiAction {
    data object LoadUser : EditProfileUiAction
    data class UpdateName(val value: String) : EditProfileUiAction
    data class UpdateSurname(val value: String) : EditProfileUiAction
    data class UpdatePhone(val value: String) : EditProfileUiAction
    data class UpdateEmail(val value: String) : EditProfileUiAction
    data class UpdateDateOfBirthday(val value: String) : EditProfileUiAction
    data class UpdateAvatar(val uri: String) : EditProfileUiAction
    data object SaveProfile : EditProfileUiAction
    data object ShowDatePicker : EditProfileUiAction
    data object HideDatePicker : EditProfileUiAction
    data object ShowImagePicker : EditProfileUiAction
    data object ProfileScreen : EditProfileUiAction
}

sealed interface EditProfileSideEffect {
    data object NavigateBack : EditProfileSideEffect
    data object OpenImagePicker : EditProfileSideEffect
}