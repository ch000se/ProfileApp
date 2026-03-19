package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke(contactId: String) {
        contactRepository.deleteContact(contactId)
    }
}
