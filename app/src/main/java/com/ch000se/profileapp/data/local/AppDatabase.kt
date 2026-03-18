package com.ch000se.profileapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.local.dao.UserDao
import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.local.entity.UserEntity


@Database(
    entities = [
        UserEntity::class,
        ContactEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
}
