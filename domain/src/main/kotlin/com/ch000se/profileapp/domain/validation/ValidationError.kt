package com.ch000se.profileapp.domain.validation

sealed class ValidationError {
    data object NameRequired : ValidationError()
    data object NameTooShort : ValidationError()

    data object SurnameRequired : ValidationError()
    data object SurnameTooShort : ValidationError()

    data object EmailRequired : ValidationError()
    data object EmailInvalid : ValidationError()

    data object PhoneRequired : ValidationError()
    data object PhoneInvalid : ValidationError()

    data object DateOfBirthdayRequired : ValidationError()
}
