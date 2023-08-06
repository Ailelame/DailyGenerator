package com.stormbirdmedia.dailygenerator.di

import com.stormbirdmedia.dailygenerator.data.local.AppDatabase
import com.stormbirdmedia.dailygenerator.data.local.buildAppDatabase
import com.stormbirdmedia.dailygenerator.data.local.provider.JokeProvider
import com.stormbirdmedia.dailygenerator.data.local.provider.UserProvider
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
    single { JokeProvider(androidContext()) }

}