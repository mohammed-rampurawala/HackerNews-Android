package com.mr.myapplication

import com.mr.myapplication.network.IHackerNewsAPI
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ProviderModule(private val hackerNewsAPI: IHackerNewsAPI) {

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Provides
    @Singleton
    fun provideAPI(): IHackerNewsAPI {
        return hackerNewsAPI
    }

}