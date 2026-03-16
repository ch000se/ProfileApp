package com.ch000se.profileapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ch000se.profileapp.data.local.entity.CategoryEntity
import com.ch000se.profileapp.data.local.entity.ContactCategoryEntity
import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.local.entity.ContactWithCategories
import com.ch000se.profileapp.domain.model.ContactCategory

@Dao
interface ContactDao {

    @Transaction
    @Query("SELECT * FROM contacts ORDER BY name ASC, surname ASC")
    suspend fun getAllContacts(): List<ContactWithCategories>

    @Transaction
    @Query(
        """
        SELECT DISTINCT contacts.*
        FROM contacts
        LEFT JOIN contact_category_entity
        ON contacts.id = contact_category_entity.contactId
        WHERE
        (:query = '' OR
         LOWER(contacts.name) LIKE '%' || LOWER(:query) || '%' OR
         LOWER(contacts.surname) LIKE '%' || LOWER(:query) || '%')
        AND
        (:categoriesSize = 0 OR contact_category_entity.categoryName IN (:categories))
        ORDER BY contacts.name ASC, contacts.surname ASC
    """
    )
    suspend fun searchWithFilters(
        query: String,
        categories: List<ContactCategory>,
        categoriesSize: Int
    ): List<ContactWithCategories>

    @Transaction
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: String): ContactWithCategories

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactCategory(contactCategory: ContactCategoryEntity)

    @Query("DELETE FROM contact_category_entity WHERE contactId = :contactId")
    suspend fun deleteContactCategories(contactId: String)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: String)

    @Transaction
    suspend fun insertContactWithCategories(
        contact: ContactEntity,
        categories: List<CategoryEntity>
    ) {
        insertContact(contact)
        deleteContactCategories(contact.id)
        categories.forEach { category ->
            insertCategory(category)
            insertContactCategory(
                ContactCategoryEntity(
                    contactId = contact.id,
                    categoryName = category.name
                )
            )
        }
    }

}