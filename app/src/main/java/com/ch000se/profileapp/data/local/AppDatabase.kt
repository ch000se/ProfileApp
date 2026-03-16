package com.ch000se.profileapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.local.dao.UserDao
import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.local.entity.UserEntity


@Database(
    entities = [UserEntity::class, ContactEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao

}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("BEGIN TRANSACTION;")
        db.execSQL("ALTER TABLE user RENAME COLUMN dateOfBirthday TO date_of_birthday")
        db.execSQL("ALTER TABLE user RENAME COLUMN avatarUri TO avatar_uri")
        db.execSQL("ALTER TABLE contacts RENAME COLUMN dateOfBirthday TO date_of_birthday")
        db.execSQL("ALTER TABLE contacts RENAME COLUMN avatarUri TO avatar_uri")
        db.execSQL("COMMIT;")
    }
}