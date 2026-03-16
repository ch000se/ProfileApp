package com.ch000se.profileapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "contact_category_entity",
    primaryKeys = ["contactId", "categoryName"],
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["name"],
            childColumns = ["categoryName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId"), Index("categoryName")]
)
data class ContactCategoryEntity(
    val contactId: String,
    val categoryName: String
)