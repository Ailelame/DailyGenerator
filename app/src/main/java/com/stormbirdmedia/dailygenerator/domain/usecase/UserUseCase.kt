package com.stormbirdmedia.dailygenerator.domain.usecase

import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.domain.models.toUser
import com.stormbirdmedia.dailygenerator.domain.models.toUserEntity
import com.stormbirdmedia.dailygenerator.data.local.provider.UserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserUseCase(private val userProvider: UserProvider) {

    suspend fun insertUser(user: User): Result<Unit> {
        return userProvider.insertUser(user.toUserEntity())
    }

    suspend fun getAllUsers(): Flow<List<User>> = userProvider.getAllUsers().map { list ->
        list.map { it.toUser() }
    }

    suspend fun updateUserSelected(userName: String, isSelected : Boolean) {
        userProvider.updateUserSelected(userName, isSelected)
    }

    fun deleteUser(user: User) {
        userProvider.deleteUser(user.toUserEntity())
    }

}