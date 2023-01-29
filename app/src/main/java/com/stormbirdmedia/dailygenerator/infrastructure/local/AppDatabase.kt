package com.stormbirdmedia.dailygenerator.infrastructure.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stormbirdmedia.dailygenerator.infrastructure.local.dao.UserDao
import com.stormbirdmedia.dailygenerator.infrastructure.local.entities.UserEntity


fun buildAppDatabase(applicationContext: Context, databaseName: String): AppDatabase {
    return Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        databaseName
    ).build()
}


@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}