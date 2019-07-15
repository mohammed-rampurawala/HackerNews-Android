package com.mr.myapplication.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.mr.myapplication.R
import com.mr.myapplication.TestHackerApplication
import com.mr.myapplication.TestHackerComponent
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.home.HackerNewsListActivity
import com.mr.myapplication.ui.news.NewsActivity
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class HackerNewsListFragmentTest {

    private lateinit var app: TestHackerApplication

    @get:Rule
    val activityRule = ActivityTestRule<HackerNewsListActivity>(HackerNewsListActivity::class.java, false, false)

    @get:Rule
    val intentActivityRule = IntentsTestRule<HackerNewsListActivity>(HackerNewsListActivity::class.java, false, false)

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
        Mockito.`when`(hackerNewsApi.getTopStories())
            .thenReturn(Single.just(Arrays.asList(2000L)).delay(2, TimeUnit.SECONDS))
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

    @Test
    fun testRetry() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories()).thenReturn(Single.error(Exception("Mock Exception")))
        activityRule.launchActivity(null)
        Thread.sleep(2000L)
        //Test on error "Retry" screen is displayed
        onView(ViewMatchers.withId(R.id.retry_view_news_list)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Mockito.`when`(hackerNewsApi.getTopStories())
            .thenReturn(Single.just(Arrays.asList(2000L)).delay(2, TimeUnit.SECONDS))
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
        //After click on retry
        onView(ViewMatchers.withId(R.id.retry)).perform(ViewActions.click())
        //List is displayed with news list
        onView(ViewMatchers.withId(R.id.loading_progress_bar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000L)
        onView(ViewMatchers.withId(R.id.news_list_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun testNewsClick() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories())
            .thenReturn(Single.just(Arrays.asList(2000L)))
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
        intentActivityRule.launchActivity(null)
        //List is displayed with news list
        onView(ViewMatchers.withId(R.id.news_list_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000L)
        // First scroll to the position that needs to be matched and click on it.
        val actionOnItemAtPosition = actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
        onView(ViewMatchers.withId(R.id.news_list_recycler_view)).perform(actionOnItemAtPosition)

        //Then news activity should be opened
        intended(hasComponent(NewsActivity::class.java.name))
        assert(Iterables.getOnlyElement(Intents.getIntents()).getBundleExtra("bundle").getParcelable<Story>("story") != null)
    }
}