package com.mr.myapplication.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.mr.myapplication.R
import com.mr.myapplication.TestHackerApplication
import com.mr.myapplication.TestHackerComponent
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.home.HackerNewsListActivity
import com.mr.myapplication.ui.home.HomeNewsListFragment
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class HackerNewsListFragmentTest {

    private lateinit var app: TestHackerApplication

    @get:Rule
    val activityRule = ActivityTestRule<HackerNewsListActivity>(HackerNewsListActivity::class.java, false, false)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestHackerApplication
    }

    @Test
    fun activityTitleIsDisplayed() {
        activityRule.launchActivity(null)
        onView(ViewMatchers.withText("Hacker News")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun testLoadingIsDisplayed() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories()).thenReturn(Single.just(Arrays.asList(2000L)).delay(2,TimeUnit.SECONDS))
        Mockito.`when`(hackerNewsApi.getData("2000")).thenReturn(
            Single.just(
                Story(
                    "by",
                    "2000",
                    Collections.emptyList(),
                    20,
                    123123L,
                    "Title",
                    "story",
                    "https://www.google.com"
                )
            )
        )
        activityRule.launchActivity(null)
        onView(ViewMatchers.withId(R.id.loading_progress_bar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000L)
        onView(ViewMatchers.withId(R.id.news_list_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun testErrorScreen() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories()).thenReturn(Single.error(Exception("Mock Exception")))
        activityRule.launchActivity(null)
        Thread.sleep(2000L)
        onView(ViewMatchers.withId(R.id.retry_view_news_list)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityRule.finishActivity()
    }

    fun getFragment(): HomeNewsListFragment? {
        return activityRule.activity.supportFragmentManager.findFragmentById(R.id.home_fragment) as HomeNewsListFragment
    }

}