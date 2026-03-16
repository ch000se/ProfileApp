package com.ch000se.profileapp.data.mapper

import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.local.entity.ContactWithCategories
import com.ch000se.profileapp.data.remote.dto.UserDto
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

fun ContactWithCategories.toDomainFromEntity(): Contact = Contact(
    id = contact.id,
    name = contact.name,
    surname = contact.surname,
    phone = contact.phone,
    email = contact.email,
    dateOfBirthday = contact.dateOfBirthday,
    avatar = contact.avatarUri,
    categories = categories.map { ContactCategory.valueOf(it.name) }
)

fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatarUri = avatar
)

fun UserDto.toDomainFromDto(): Contact = Contact(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatar = avatar,
    categories = emptyList()
)


fun List<UserDto>.toDomainListFromDto(): List<Contact> = map { it.toDomainFromDto() }
