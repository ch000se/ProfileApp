package com.ch000se.profileapp.domain.repository

import com.ch000se.profileapp.domain.model.User

interface UserRepository {
    suspend fun getUser(): User?
    suspend fun saveUser(user: User)
    suspend fun isUserExist(): Boolean
    suspend fun deleteUser()
}
