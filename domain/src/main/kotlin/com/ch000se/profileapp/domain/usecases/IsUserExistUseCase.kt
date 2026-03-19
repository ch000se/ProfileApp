package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.repository.UserRepository
import javax.inject.Inject

class IsUserExistUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Boolean = repository.isUserExist()
}
