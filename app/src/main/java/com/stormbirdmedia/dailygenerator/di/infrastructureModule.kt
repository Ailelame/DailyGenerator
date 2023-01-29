package com.stormbirdmedia.dailygenerator.di

import com.stormbirdmedia.dailygenerator.infrastructure.local.AppDatabase
import com.stormbirdmedia.dailygenerator.infrastructure.local.buildAppDatabase
import com.stormbirdmedia.dailygenerator.infrastructure.local.provider.UserProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val DATABASE_NAME = "dailygen_database"

val infrastructureModule = module {
    single {
        buildAppDatabase(androidContext(), DATABASE_NAME)
    }
    single {
        get<AppDatabase>().userDao()
    }
    factory { UserProvider(get()) }

}