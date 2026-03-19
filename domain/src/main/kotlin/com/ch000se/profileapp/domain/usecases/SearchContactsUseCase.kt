package com.ch000se.profileapp.domain.usecases

import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory
import com.ch000se.profileapp.domain.repository.ContactRepository
import javax.inject.Inject

class SearchContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(query: String, categories: List<ContactCategory>): List<Contact> =
        repository.searchWithFilters(query, categories)
}
