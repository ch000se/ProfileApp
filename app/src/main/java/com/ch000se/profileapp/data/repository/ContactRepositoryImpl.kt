package com.ch000se.profileapp.data.repository

import com.ch000se.ninjauser.data.remote.RandomUserApi
import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.mapper.toDomainFromEntity
import com.ch000se.profileapp.data.mapper.toDomainListFromDto
import com.ch000se.profileapp.data.mapper.toDomainListFromEntities
import com.ch000se.profileapp.data.mapper.toEntity
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val randomUserApi: RandomUserApi
) : ContactRepository {

    override suspend fun getAllContacts(): List<Contact> {
        return contactDao.getAllContacts().toDomainListFromEntities()
    }

    override suspend fun getContactById(contactId: String): Contact {
        return contactDao.getContactById(contactId).toDomainFromEntity()
    }

    override suspend fun addContact(contact: Contact): Long {
        val entity = contact.toEntity()
        return contactDao.insertContact(entity)
    }


    override suspend fun deleteContact(contactId: String) {
        contactDao.deleteContactById(contactId)
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