package com.ch000se.profileapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ch000se.profileapp.data.local.entity.ContactEntity

@Dao
interface ContactDao {

    @Query(
        """
        SELECT * FROM contacts
        WHERE :query = ''
            OR (name || ' ' || surname) LIKE '%' || :query || '%'
            OR (surname || ' ' || name) LIKE '%' || :query || '%'
        ORDER BY name ASC, surname ASC
        """
    )
    suspend fun searchByQuery(query: String): List<ContactEntity>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: String): ContactEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContactById(id: String)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}
