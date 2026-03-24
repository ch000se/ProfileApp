package com.ch000se.profileapp.data.repository

import com.ch000se.profileapp.data.local.dao.UserDao
import com.ch000se.profileapp.data.local.entity.UserEntity
import com.ch000se.profileapp.data.local.internal.ImageFileManager
import com.ch000se.profileapp.domain.model.User
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("UserRepository")
class UserRepositoryTest {

    @MockK
    private lateinit var userDao: UserDao

    @MockK(relaxed = true)
    private lateinit var imageFileManager: ImageFileManager

    private lateinit var repository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = UserRepositoryImpl(userDao, imageFileManager)
    }

    @Nested
    @DisplayName("saveUser()")
    inner class SaveUser {

        @Test
        fun `GIVEN new user with external avatar WHEN saveUser THEN copies avatar to internal storage`() = runTest {
            val externalPath = "content://external/avatar.jpg"
            val internalPath = "/data/files/IMG_123.jpg"
            val user = createUser(avatar = externalPath)
            coEvery { userDao.getUser() } returns null
            coEvery { imageFileManager.isInternal(externalPath) } returns false
            coEvery { imageFileManager.copyImageToInternalStorage(externalPath) } returns internalPath
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(user)

            coVerify(exactly = 1) { imageFileManager.copyImageToInternalStorage(externalPath) }
            coVerify(exactly = 1) { userDao.insertUser(any()) }
        }

        @Test
        fun `GIVEN new user with internal avatar WHEN saveUser THEN saves without copying`() = runTest {
            val internalPath = "/data/files/IMG_123.jpg"
            val user = createUser(avatar = internalPath)
            coEvery { userDao.getUser() } returns null
            coEvery { imageFileManager.isInternal(internalPath) } returns true
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(user)

            coVerify(exactly = 0) { imageFileManager.copyImageToInternalStorage(any()) }
            coVerify(exactly = 1) { userDao.insertUser(any()) }
        }

        @Test
        fun `GIVEN new user with empty avatar WHEN saveUser THEN saves without processing`() = runTest {
            val user = createUser(avatar = "")
            coEvery { userDao.getUser() } returns null
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(user)

            coVerify(exactly = 0) { imageFileManager.isInternal(any()) }
            coVerify(exactly = 0) { imageFileManager.copyImageToInternalStorage(any()) }
        }

        @Test
        fun `GIVEN existing user with same avatar WHEN saveUser THEN does not delete old avatar`() = runTest {
            val avatarPath = "/data/files/IMG_123.jpg"
            val oldEntity = createUserEntity(avatar = avatarPath)
            val newUser = createUser(avatar = avatarPath)
            coEvery { userDao.getUser() } returns oldEntity
            coEvery { imageFileManager.isInternal(avatarPath) } returns true
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(newUser)

            coVerify(exactly = 0) { imageFileManager.deleteImage(any()) }
        }

        @Test
        fun `GIVEN existing user with different avatar WHEN saveUser THEN deletes old avatar`() = runTest {
            val oldPath = "/data/files/IMG_OLD.jpg"
            val newPath = "/data/files/IMG_NEW.jpg"
            val oldEntity = createUserEntity(avatar = oldPath)
            val newUser = createUser(avatar = newPath)
            coEvery { userDao.getUser() } returns oldEntity
            coEvery { imageFileManager.isInternal(newPath) } returns true
            coEvery { imageFileManager.deleteImage(oldPath) } returns Unit
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(newUser)

            coVerify(exactly = 1) { imageFileManager.deleteImage(oldPath) }
        }

        @Test
        fun `GIVEN existing user with avatar WHEN saveUser with empty avatar THEN deletes old avatar`() = runTest {
            val oldPath = "/data/files/IMG_OLD.jpg"
            val oldEntity = createUserEntity(avatar = oldPath)
            val newUser = createUser(avatar = "")
            coEvery { userDao.getUser() } returns oldEntity
            coEvery { imageFileManager.deleteImage(oldPath) } returns Unit
            coEvery { userDao.insertUser(any()) } returns Unit

            repository.saveUser(newUser)

            coVerify(exactly = 1) { imageFileManager.deleteImage(oldPath) }
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    inner class DeleteUser {

        @Test
        fun `GIVEN user with avatar WHEN deleteUser THEN deletes avatar file`() = runTest {
            val avatarPath = "/data/files/IMG_123.jpg"
            val entity = createUserEntity(avatar = avatarPath)
            coEvery { userDao.getUser() } returns entity
            coEvery { imageFileManager.deleteImage(avatarPath) } returns Unit
            coEvery { userDao.deleteUser() } returns Unit

            repository.deleteUser()

            coVerify(exactly = 1) { imageFileManager.deleteImage(avatarPath) }
            coVerify(exactly = 1) { userDao.deleteUser() }
        }

        @Test
        fun `GIVEN no user in database WHEN deleteUser THEN does not call deleteImage`() = runTest {
            coEvery { userDao.getUser() } returns null
            coEvery { userDao.deleteUser() } returns Unit

            repository.deleteUser()

            coVerify(exactly = 0) { imageFileManager.deleteImage(any()) }
            coVerify(exactly = 1) { userDao.deleteUser() }
        }
    }

    private fun createUserEntity(
        name: String = "Andriy",
        surname: String = "Bondarenko",
        phone: String = "+380931234567",
        email: String = "andriy.bondarenko@gmail.com",
        dateOfBirthday: String = "1988-11-28",
        avatar: String = "avatar.jpg"
    ) = UserEntity(
        id = 1,
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatarUri = avatar
    )

    private fun createUser(
        name: String = "Andriy",
        surname: String = "Bondarenko",
        phone: String = "+380931234567",
        email: String = "andriy.bondarenko@gmail.com",
        dateOfBirthday: String = "1988-11-28",
        avatar: String = "avatar.jpg"
    ) = User(
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatar = avatar
    )
}
