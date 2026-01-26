package com.ivangarzab.kluvs

import android.app.Application
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.kluvs.app.initializeSentry
import com.ivangarzab.kluvs.di.initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeSentry()
        initLogging()
        initDependencyInjection()
    }

    private fun initLogging() = Bark.train(
        AndroidLogTrainer(
            if (BuildConfig.DEBUG) {
                Level.VERBOSE
            } else {
                Level.WARNING
            }
        )
    )

    private fun initDependencyInjection() {
        initKoin {
            androidContext(this@App)
        }
    }
}