package com.ch000se.profileapp.domain.validation

enum class UserField {
    NAME, SURNAME, EMAIL, PHONE, DATE_OF_BIRTHDAY
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val errors: Map<UserField, ValidationError>) : ValidationResult()
}