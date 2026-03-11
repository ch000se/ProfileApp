package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User?> = repository.getUser()
}