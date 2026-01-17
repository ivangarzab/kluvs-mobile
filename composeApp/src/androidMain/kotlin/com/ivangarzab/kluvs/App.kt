package com.ivangarzab.kluvs

import android.app.Application
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.kluvs.di.initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Bark.train(AndroidLogTrainer())
        initKoin {
            androidContext(this@App)
        }
    }
}