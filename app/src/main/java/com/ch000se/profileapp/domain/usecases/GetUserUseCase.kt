package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): User? = repository.getUser()
}