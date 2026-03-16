package com.ch000se.profileapp.domain.repository

import com.ch000se.profileapp.domain.model.Contact

interface ContactRepository {
    suspend fun getAllContacts(): List<Contact>
    suspend fun getContactById(contactId: String): Contact
    suspend fun addContact(contact: Contact): Long
    suspend fun deleteContact(contactId: String)
    suspend fun getRandomUsers(count: Int = 10): Result<List<Contact>>
}