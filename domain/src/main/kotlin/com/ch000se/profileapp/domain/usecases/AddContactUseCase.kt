package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact) {
        return contactRepository.addContact(contact)
    }
}
