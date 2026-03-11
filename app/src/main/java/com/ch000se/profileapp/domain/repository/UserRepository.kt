package com.ch000se.profileapp.domain.repository

import com.ch000se.profileapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun isUserExist(): Boolean
}