package com.ch000se.profileapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ContactWithCategories(
    @Embedded
    val contact: ContactEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(
            value = ContactCategoryEntity::class,
            parentColumn = "contactId",
            entityColumn = "categoryName"
        )
    )
    val categories: List<CategoryEntity>
)