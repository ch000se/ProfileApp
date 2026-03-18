package com.ch000se.profileapp.domain.repository

import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

interface ContactRepository {
    suspend fun searchWithFilters(query: String, categories: List<ContactCategory>): List<Contact>
    suspend fun getContactById(contactId: String): Contact
    suspend fun addContact(contact: Contact)
    suspend fun deleteContact(contactId: String)
    suspend fun deleteAllContacts()
    suspend fun getRandomUsers(count: Int = 10): Result<List<Contact>>
}