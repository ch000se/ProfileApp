package com.ch000se.profileapp.data.repository

import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.mapper.toDomainFromEntity
import com.ch000se.profileapp.data.mapper.toDomainListFromDto
import com.ch000se.profileapp.data.mapper.toEntity
import com.ch000se.profileapp.data.remote.api.RandomUserApi
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

internal class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val randomUserApi: RandomUserApi
) : ContactRepository {

    override suspend fun searchWithFilters(
        query: String,
        categories: List<ContactCategory>
    ): List<Contact> {
        return contactDao.searchByQuery(query)
            .map { it.toDomainFromEntity() }
            .filter { contact -> contact.categories.any { it in categories } }
    }

    override suspend fun getContactById(contactId: String): Contact {
        return contactDao.getContactById(contactId).toDomainFromEntity()
    }

    override suspend fun addContact(contact: Contact) {
        contactDao.insertContact(contact.toEntity())
    }

    override suspend fun deleteContact(contactId: String) {
        contactDao.deleteContactById(contactId)
    }

    override suspend fun deleteAllContacts() {
        contactDao.deleteAll()
    }

    override suspend fun getRandomUsers(count: Int): Result<List<Contact>> {
        return try {
            val response = randomUserApi.getUsers(count)
            val users = response.toDomainListFromDto()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
