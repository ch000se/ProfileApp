package com.ch000se.profileapp.domain.validation

import com.ch000se.profileapp.domain.model.User
import javax.inject.Inject

class UserValidator @Inject constructor() {

    companion object {
        private const val MIN_NAME_LENGTH = 2
        private val PHONE_REGEX = Regex("^\\+380\\d{9}$")
        private val EMAIL_REGEX = Regex(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
        )
    }

    fun validate(user: User): ValidationResult {
        val errors = mutableMapOf<UserField, ValidationError>()

        validateName(user.name)?.let { errors[UserField.NAME] = it }
        validateSurname(user.surname)?.let { errors[UserField.SURNAME] = it }
        validateEmail(user.email)?.let { errors[UserField.EMAIL] = it }
        validatePhone(user.phone)?.let { errors[UserField.PHONE] = it }
        validateDateOfBirthday(user.dateOfBirthday)?.let { errors[UserField.DATE_OF_BIRTHDAY] = it }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    fun validateName(name: String): ValidationError? {
        return when {
            name.isBlank() -> ValidationError.NameRequired
            name.length < MIN_NAME_LENGTH -> ValidationError.NameTooShort
            else -> null
        }
    }

    fun validateSurname(surname: String): ValidationError? {
        return when {
            surname.isBlank() -> ValidationError.SurnameRequired
            surname.length < MIN_NAME_LENGTH -> ValidationError.SurnameTooShort
            else -> null
        }
    }

    fun validateEmail(email: String): ValidationError? {
        return when {
            email.isBlank() -> ValidationError.EmailRequired
            !EMAIL_REGEX.matches(email) -> ValidationError.EmailInvalid
            else -> null
        }
    }

    fun validatePhone(phone: String): ValidationError? {
        return when {
            phone.isBlank() -> ValidationError.PhoneRequired
            !PHONE_REGEX.matches(phone) -> ValidationError.PhoneInvalid
            else -> null
        }
    }

    fun validateDateOfBirthday(date: String): ValidationError? {
        return if (date.isBlank()) ValidationError.DateOfBirthdayRequired else null
    }
}
