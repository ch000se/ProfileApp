package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.repository.UserRepository
import com.ch000se.profileapp.domain.validation.UserValidator
import com.ch000se.profileapp.domain.validation.ValidationResult
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val validator: UserValidator
) {
    suspend operator fun invoke(user: User): ValidationResult {
        val result = validator.validate(user)
        if (result is ValidationResult.Success) {
            repository.saveUser(user)
        }
        return result
    }
}
