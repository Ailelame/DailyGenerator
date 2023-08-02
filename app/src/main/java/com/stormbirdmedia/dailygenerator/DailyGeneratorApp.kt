package com.stormbirdmedia.dailygenerator

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.stormbirdmedia.dailygenerator.di.appModule
import com.stormbirdmedia.dailygenerator.di.domainModule
import com.stormbirdmedia.dailygenerator.di.infrastructureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class DailyGeneratorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DailyGeneratorApp)
            modules(appModule, infrastructureModule, domainModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}