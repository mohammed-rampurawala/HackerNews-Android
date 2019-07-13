package com.mr.myapplication.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

@Component(modules = [AndroidSupportInjectionModule::class, HackerModule::class, ProviderModule::class])
@Singleton
interface HackerComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindApp(app: Application): Builder

        fun build(): HackerComponent
    }
}