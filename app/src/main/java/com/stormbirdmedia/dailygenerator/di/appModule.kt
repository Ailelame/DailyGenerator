package com.stormbirdmedia.dailygenerator.di

import com.stormbirdmedia.dailygenerator.screen.addUser.AddUserViewModel
import com.stormbirdmedia.dailygenerator.screen.joke.JokeViewModel
import com.stormbirdmedia.dailygenerator.screen.main.MainViewModel
import com.stormbirdmedia.dailygenerator.screen.randomizer.RandomizerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { AddUserViewModel(get()) }
    viewModel { RandomizerViewModel(androidContext(), get()) }
    viewModel { JokeViewModel(get()) }
}