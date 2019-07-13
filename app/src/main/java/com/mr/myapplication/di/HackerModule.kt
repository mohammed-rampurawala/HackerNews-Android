package com.mr.myapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mr.myapplication.core.HackerVMFactory
import com.mr.myapplication.ui.HackerViewModel
import com.mr.myapplication.ui.home.HackerNewsListActivity
import com.mr.myapplication.ui.home.HomeNewsListFragment
import com.mr.myapplication.ui.news.NewsActivity
import com.mr.myapplication.ui.news.NewsFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Hacker module will provide dependencies for the application level scope
 */
@Module
abstract class HackerModule {

    @ContributesAndroidInjector
    abstract fun bindHackerNewsListActivity(): HackerNewsListActivity

    @ContributesAndroidInjector
    abstract fun bindMainFragment(): HomeNewsListFragment

    @ContributesAndroidInjector
    abstract fun bindNewsActivity(): NewsActivity

    @ContributesAndroidInjector
    abstract fun bindNewsFragment(): NewsFragment

    @Binds
    @IntoMap
    @ViewModelKey(HackerViewModel::class)
    abstract fun bindHackerViewModel(hackerViewModel: HackerViewModel): ViewModel

    @Binds
    abstract fun bindHackerVMFactory(factory: HackerVMFactory): ViewModelProvider.Factory
}