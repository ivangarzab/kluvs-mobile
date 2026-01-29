package com.ivangarzab.kluvs

import android.app.Application
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.kluvs.util.initializeSentry
import com.ivangarzab.kluvs.di.initKoin
import com.ivangarzab.kluvs.util.SentryTrainer
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeSentry()
        initLogging()
        initDependencyInjection()
    }

    private fun initLogging() = Bark.apply {
        train(
            AndroidLogTrainer(
                if (BuildConfig.DEBUG) Level.VERBOSE else Level.WARNING
            )
        )
        train(SentryTrainer())
    }

    private fun initDependencyInjection() {
        initKoin {
            androidContext(this@App)
        }
    }
}