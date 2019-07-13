package com.mr.myapplication

import com.mr.myapplication.di.DaggerHackerComponent
import com.mr.myapplication.di.HackerComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber.DebugTree
import timber.log.Timber


class HackerApplication : DaggerApplication() {

    lateinit var hackerComponent: HackerComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        initComponent()
        return hackerComponent
    }

    private fun initComponent() {
        if (!this::hackerComponent.isInitialized) {
            hackerComponent = DaggerHackerComponent.builder().bindApp(this).build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        initComponent()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}