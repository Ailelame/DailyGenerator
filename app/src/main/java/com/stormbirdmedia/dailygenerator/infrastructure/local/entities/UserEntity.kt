package com.stormbirdmedia.dailygenerator.infrastructure.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "weight")
    var weight : Int = 0,
    @ColumnInfo(name = "isEnabled")
    var isEnabled : Boolean = true
)