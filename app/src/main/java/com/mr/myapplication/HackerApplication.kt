package com.mr.myapplication

import com.mr.myapplication.di.DaggerHackerComponent
import com.mr.myapplication.di.HackerComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree


open class HackerApplication : DaggerApplication() {

    lateinit var hackerComponent: HackerComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        initComponent()
        return hackerComponent
    }

    protected open fun initComponent() {
        hackerComponent = DaggerHackerComponent.builder().bindApp(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}