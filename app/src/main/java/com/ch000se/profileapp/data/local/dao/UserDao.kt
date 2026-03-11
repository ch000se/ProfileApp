package com.ch000se.profileapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ch000se.profileapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): Flow<UserEntity?>


    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE id = 1)")
    suspend fun isUserExists(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)
}