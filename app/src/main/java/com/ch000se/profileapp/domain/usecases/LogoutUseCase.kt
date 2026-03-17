package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.repository.ContactRepository
import com.ch000se.profileapp.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository
) {
    suspend operator fun invoke() {
        contactRepository.deleteAllContacts()
        userRepository.deleteUser()
    }
}