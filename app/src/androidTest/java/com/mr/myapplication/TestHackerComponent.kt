package com.mr.myapplication

import com.mr.myapplication.di.HackerComponent
import com.mr.myapplication.network.IHackerNewsAPI
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        TestHackerModule::class, ProviderModule::class]
)
interface TestHackerComponent : HackerComponent {
    fun getHackerNewsApi(): IHackerNewsAPI
}