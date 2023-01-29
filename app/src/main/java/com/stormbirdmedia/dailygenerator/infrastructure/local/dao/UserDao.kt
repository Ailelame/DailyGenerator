package com.stormbirdmedia.dailygenerator.infrastructure.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.stormbirdmedia.dailygenerator.infrastructure.local.base.BaseDao
import com.stormbirdmedia.dailygenerator.infrastructure.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE name = :name")
    fun getUserByName(name: String): UserEntity?

    @Query("UPDATE user SET isEnabled = :isSelected WHERE name = :userName")
    suspend fun updateUserSelected(userName: String, isSelected : Boolean)
}
