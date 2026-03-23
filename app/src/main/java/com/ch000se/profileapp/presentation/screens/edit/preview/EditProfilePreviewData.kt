package com.ch000se.profileapp.presentation.screens.edit.preview

import com.ch000se.profileapp.domain.validation.UserField
import com.ch000se.profileapp.domain.validation.ValidationError
import com.ch000se.profileapp.presentation.screens.edit.EditProfileUiState

internal object EditProfilePreviewData {

    val sampleEditProfileUiState = EditProfileUiState(
        name = "John",
        surname = "Doe",
        phone = "+380501234567",
        email = "john.doe@example.com",
        dateOfBirthday = "15.03.1990",
        avatarUri = "",
        validationErrors = emptyMap(),
        isLoading = false,
        showDatePicker = false,
        isSaveButtonEnabled = true
    )

    val sampleEditProfileUiStateEmpty = EditProfileUiState()

    val sampleEditProfileUiStateLoading = sampleEditProfileUiState.copy(
        isLoading = true
    )

    val sampleEditProfileUiStateWithErrors = EditProfileUiState(
        name = "",
        surname = "",
        phone = "invalid",
        email = "invalid-email",
        dateOfBirthday = "",
        avatarUri = "",
        validationErrors = mapOf(
            UserField.NAME to ValidationError.NameRequired,
            UserField.SURNAME to ValidationError.SurnameRequired,
            UserField.EMAIL to ValidationError.EmailInvalid,
            UserField.PHONE to ValidationError.PhoneInvalid
        ),
        isLoading = false,
        showDatePicker = false,
        isSaveButtonEnabled = false
    )
}
