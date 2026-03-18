package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

class GetContactByIdUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contactId: String): Contact = repository.getContactById(contactId)
}