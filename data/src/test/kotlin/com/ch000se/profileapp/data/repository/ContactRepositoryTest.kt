package com.ch000se.profileapp.data.repository

import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.remote.api.RandomUserApi
import com.ch000se.profileapp.data.remote.dto.UserDto
import com.ch000se.profileapp.domain.model.ContactCategory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.IOException

@DisplayName("ContactRepository")
class ContactRepositoryTest {

    @MockK
    private lateinit var contactDao: ContactDao

    @MockK
    private lateinit var randomUserApi: RandomUserApi

    private lateinit var repository: ContactRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ContactRepositoryImpl(contactDao, randomUserApi)
    }

    @Nested
    @DisplayName("searchWithFilters()")
    inner class SearchWithFilters {

        @Test
        fun `GIVEN contacts with different categories WHEN searchWithFilters THEN returns only matching`() = runTest {
            val entities = listOf(
                createContactEntity(id = "1", categories = listOf(ContactCategory.FAMILY)),
                createContactEntity(id = "2", categories = listOf(ContactCategory.FRIENDS)),
                createContactEntity(id = "3", categories = listOf(ContactCategory.WORK, ContactCategory.FAMILY))
            )
            val expectedSize = 2
            coEvery { contactDao.searchByQuery("") } returns entities

            val result = repository.searchWithFilters("", listOf(ContactCategory.FAMILY))

            assertEquals(expectedSize, result.size)
            assertTrue(result.all { contact -> contact.categories.contains(ContactCategory.FAMILY) })
            coVerify(exactly = 1) { contactDao.searchByQuery("") }
        }

        @Test
        fun `GIVEN contacts WHEN searchWithFilters with non-matching category THEN returns empty list`() = runTest {
            val entities = listOf(
                createContactEntity(id = "1", categories = listOf(ContactCategory.FAMILY)),
                createContactEntity(id = "2", categories = listOf(ContactCategory.FRIENDS))
            )
            coEvery { contactDao.searchByQuery("") } returns entities

            val result = repository.searchWithFilters("", listOf(ContactCategory.WORK))

            assertTrue(result.isEmpty())
            coVerify(exactly = 1) { contactDao.searchByQuery("") }
        }
    }

    @Nested
    @DisplayName("getRandomUsers()")
    inner class GetRandomUsers {

        @Test
        fun `GIVEN API returns users WHEN getRandomUsers THEN returns success with mapped contacts`() = runTest {
            val userDtos = listOf(
                createUserDto(id = "1", name = "Taras"),
                createUserDto(id = "2", name = "Oksana")
            )
            val expectedSize = 2
            coEvery { randomUserApi.getUsers(5) } returns userDtos

            val result = repository.getRandomUsers(5)

            assertTrue(result.isSuccess)
            assertEquals(expectedSize, result.getOrNull()?.size)
            coVerify(exactly = 1) { randomUserApi.getUsers(5) }
        }

        @Test
        fun `GIVEN API throws exception WHEN getRandomUsers THEN returns failure`() = runTest {
            val expectedException = IOException("Network error")
            coEvery { randomUserApi.getUsers(5) } throws expectedException

            val result = repository.getRandomUsers(5)

            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
            coVerify(exactly = 1) { randomUserApi.getUsers(5) }
        }
    }

    private fun createContactEntity(
        id: String = "1",
        name: String = "Oleksandr",
        surname: String = "Shevchenko",
        phone: String = "+380501234567",
        email: String = "oleksandr.shevchenko@gmail.com",
        dateOfBirthday: String = "1995-03-15",
        avatarUri: String = "avatar.jpg",
        categories: List<ContactCategory> = emptyList()
    ) = ContactEntity(
        id = id,
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        dateOfBirthday = dateOfBirthday,
        avatarUri = avatarUri,
        categories = categories
    )

    private fun createUserDto(
        id: String = "1",
        name: String = "Mariia",
        surname: String = "Kovalenko",
        email: String = "mariia.kovalenko@ukr.net",
        avatar: String = "avatar.jpg",
        phone: String = "+380671234567",
        dateOfBirthday: String = "1992-07-22"
    ) = UserDto(
        id = id,
        name = name,
        surname = surname,
        email = email,
        avatar = avatar,
        phone = phone,
        dateOfBirthday = dateOfBirthday
    )
}
