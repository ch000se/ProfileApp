package com.ch000se.profileapp.data.repository

import com.ch000se.profileapp.data.local.dao.UserDao
import com.ch000se.profileapp.data.local.internal.ImageFileManager
import com.ch000se.profileapp.data.mapper.toDomain
import com.ch000se.profileapp.data.mapper.toEntity
import com.ch000se.profileapp.domain.model.User
import com.ch000se.profileapp.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val imageFileManager: ImageFileManager
) : UserRepository {

    override suspend fun getUser(): User? {
        return userDao.getUser()?.toDomain()
    }

    override suspend fun saveUser(user: User) {
        val oldUser = userDao.getUser()?.toDomain()

        val oldAvatarUrl = oldUser?.avatar
        val newAvatarUrl = user.avatar

        if (oldAvatarUrl != null && oldAvatarUrl != newAvatarUrl) {
            imageFileManager.deleteImage(oldAvatarUrl)
        }

        val processedUser = user.processForStorage()
        userDao.insertUser(processedUser.toEntity())
    }

    override suspend fun isUserExist(): Boolean {
        return userDao.isUserExists()
    }

    override suspend fun deleteUser() {
        val user = userDao.getUser()?.toDomain()
        user?.avatar?.let { imageFileManager.deleteImage(it) }
        userDao.deleteUser()
    }

    private suspend fun User.processForStorage(): User {
        return if (avatar.isNotEmpty() && !imageFileManager.isInternal(avatar)) {
            val internalPath = imageFileManager.copyImageToInternalStorage(avatar)
            copy(avatar = internalPath)
        } else {
            this
        }
    }
}
