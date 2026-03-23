package com.ch000se.profileapp.data.mapper

import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.remote.dto.UserDto
import com.ch000se.profileapp.domain.model.Contact

internal fun ContactEntity.toDomainFromEntity(): Contact = Contact(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatar = avatarUri,
    categories = categories
)

internal fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatarUri = avatar,
    categories = categories
)

internal fun UserDto.toDomainFromDto(): Contact = Contact(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatar = avatar,
    categories = emptyList()
)

internal fun List<UserDto>.toDomainListFromDto(): List<Contact> = map { it.toDomainFromDto() }
