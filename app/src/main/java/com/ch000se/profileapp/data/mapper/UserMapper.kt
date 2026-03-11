package com.ch000se.profileapp.data.mapper

import com.ch000se.profileapp.data.local.entity.UserEntity
import com.ch000se.profileapp.domain.model.User

fun UserEntity.toDomain(): User = User(
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatar = avatarUri
)

fun User.toEntity(): UserEntity = UserEntity(
    id = 1,
    name = name,
    surname = surname,
    phone = phone,
    email = email,
    dateOfBirthday = dateOfBirthday,
    avatarUri = avatar
)