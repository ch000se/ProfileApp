package com.ch000se.profileapp.domain.validation

import com.ch000se.profileapp.domain.model.User

interface UserValidator {
    fun validate(user: User): ValidationResult
    fun validateName(name: String): ValidationError?
    fun validateSurname(surname: String): ValidationError?
    fun validateEmail(email: String): ValidationError?
    fun validatePhone(phone: String): ValidationError?
    fun validateDateOfBirthday(date: String): ValidationError?
}