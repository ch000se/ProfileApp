package com.ch000se.profileapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ch000se.profileapp.data.local.dao.ContactDao
import com.ch000se.profileapp.data.local.dao.UserDao
import com.ch000se.profileapp.data.local.entity.CategoryEntity
import com.ch000se.profileapp.data.local.entity.ContactCategoryEntity
import com.ch000se.profileapp.data.local.entity.ContactEntity
import com.ch000se.profileapp.data.local.entity.UserEntity


@Database(
    entities = [
        UserEntity::class,
        ContactEntity::class,
        CategoryEntity::class,
        ContactCategoryEntity::class
    ],
    version = 6,
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

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories (
                name TEXT NOT NULL PRIMARY KEY
            )
            """.trimIndent()
        )

        db.execSQL("INSERT OR IGNORE INTO categories (name) VALUES ('FAMILY')")
        db.execSQL("INSERT OR IGNORE INTO categories (name) VALUES ('FRIENDS')")
        db.execSQL("INSERT OR IGNORE INTO categories (name) VALUES ('WORK')")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS contact_category_entity (
                contactId TEXT NOT NULL,
                categoryName TEXT NOT NULL,
                PRIMARY KEY (contactId, categoryName),
                FOREIGN KEY (contactId) REFERENCES contacts(id) ON DELETE CASCADE,
                FOREIGN KEY (categoryName) REFERENCES categories(name) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS index_contact_category_entity_contactId ON contact_category_entity(contactId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_contact_category_entity_categoryName ON contact_category_entity(categoryName)")

        db.execSQL(
            """
            INSERT INTO contact_category_entity (contactId, categoryName)
            SELECT id, 'FAMILY'
            FROM contacts
            WHERE categories LIKE '%FAMILY%'
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO contact_category_entity (contactId, categoryName)
            SELECT id, 'FRIENDS'
            FROM contacts
            WHERE categories LIKE '%FRIENDS%'
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO contact_category_entity (contactId, categoryName)
            SELECT id, 'WORK'
            FROM contacts
            WHERE categories LIKE '%WORK%'
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE contacts_new (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                surname TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT NOT NULL,
                date_of_birthday TEXT NOT NULL,
                avatar_uri TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO contacts_new (id, name, surname, phone, email, date_of_birthday, avatar_uri)
            SELECT id, name, surname, phone, email, date_of_birthday, avatar_uri
            FROM contacts
            """.trimIndent()
        )

        db.execSQL("DROP TABLE contacts")

        db.execSQL("ALTER TABLE contacts_new RENAME TO contacts")
    }
}