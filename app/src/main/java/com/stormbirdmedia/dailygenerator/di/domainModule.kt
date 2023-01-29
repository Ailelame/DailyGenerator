package com.stormbirdmedia.dailygenerator.di

import com.stormbirdmedia.dailygenerator.domain.usecase.UserUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { UserUseCase(get()) }
}