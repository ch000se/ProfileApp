package com.ch000se.profileapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val dateOfBirthday: String,
    val avatarUri: String,
    val categories: String
)