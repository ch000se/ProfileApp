package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    operator fun invoke(): Flow<List<Contact>> {
        return contactRepository.getAllContacts()
    }
}
