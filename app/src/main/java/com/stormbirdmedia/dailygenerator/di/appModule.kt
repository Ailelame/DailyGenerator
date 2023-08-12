package com.stormbirdmedia.dailygenerator.di

import com.stormbirdmedia.dailygenerator.screen.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get(), get()) }
}