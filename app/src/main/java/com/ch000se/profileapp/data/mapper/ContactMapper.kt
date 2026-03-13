package com.ch000se.profileapp.data.mapper

import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.remote.dto.UserDto
import com.ch000se.profileapp.domain.model.Contact
import com.ch000se.profileapp.domain.model.ContactCategory

fun ContactEntity.toDomainFromEntity(): Contact = Contact(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatar = avatarUri,
    categories = categories.split(",")
        .filter { it.isNotBlank() }
        .map { ContactCategory.valueOf(it) }
)

fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatarUri = avatar,
    categories = categories.joinToString(",") { it.name }
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


fun List<ContactEntity>.toDomainListFromEntities(): List<Contact> = map { it.toDomainFromEntity() }
fun List<UserDto>.toDomainListFromDto(): List<Contact> = map { it.toDomainFromDto() }
