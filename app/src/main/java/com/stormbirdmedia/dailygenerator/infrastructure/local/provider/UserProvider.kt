package com.stormbirdmedia.dailygenerator.infrastructure.local.provider

import com.stormbirdmedia.dailygenerator.infrastructure.local.dao.UserDao
import com.stormbirdmedia.dailygenerator.infrastructure.local.entities.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserProvider(private val userDao: UserDao) {
    suspend fun insertUser(user: UserEntity): Result<Unit> = withContext(Dispatchers.IO) {
        if (userDao.getUserByName(user.name) == null) {
            userDao.insert(user)
            Result.success(Unit)
        } else
            Result.failure(Exception("User with name ${user.name} already exists"))

    }

    suspend fun getAllUsers(): Flow<List<UserEntity>> = withContext(Dispatchers.IO) {
        userDao.getAll()
    }

    suspend fun updateUserSelected(userName: String, isSelected : Boolean) = withContext(Dispatchers.IO) {
        userDao.updateUserSelected(userName, isSelected)
    }

    fun deleteUser(entity: UserEntity) {
        userDao.delete(entity)
    }
}
