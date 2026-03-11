package com.ch000se.profileapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val dateOfBirthday: String,
    val avatarUri: String
)