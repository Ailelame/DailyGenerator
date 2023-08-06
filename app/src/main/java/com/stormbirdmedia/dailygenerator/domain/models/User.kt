package com.stormbirdmedia.dailygenerator.domain.models

import com.stormbirdmedia.dailygenerator.data.local.entities.UserEntity

data class User (
    val name: String,
    val id: Int = 0,
    var weight : Int = 0,
    var isSelected : Boolean = true
)

fun UserEntity.toUser() = User(
    id = id,
    name = name,
    weight = weight,
    isSelected = isEnabled
)

fun User.toUserEntity() = UserEntity(
    id = id,
    name = name,
    weight = weight,
    isEnabled = isSelected
)