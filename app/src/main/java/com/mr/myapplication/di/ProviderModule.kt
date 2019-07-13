package com.mr.myapplication.di

import com.mr.myapplication.network.IHackerNewsAPI
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ProviderModule {

    @Provides
    @Singleton
    fun provideHackerNewsApi(): IHackerNewsAPI {
        return Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IHackerNewsAPI::class.java)
    }

    @Provides
    fun provideDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}