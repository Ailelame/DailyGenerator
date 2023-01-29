package com.stormbirdmedia.dailygenerator

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.stormbirdmedia.dailygenerator.di.appModule
import com.stormbirdmedia.dailygenerator.di.domainModule
import com.stormbirdmedia.dailygenerator.di.infrastructureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class DailyGeneratorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this, R.style.Theme_DailyGenerator)
        startKoin {
            androidLogger()
            androidContext(this@DailyGeneratorApp)
            modules(appModule, infrastructureModule, domainModule)
        }
    }
}