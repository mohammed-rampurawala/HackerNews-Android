package com.mr.myapplication.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.mr.myapplication.R
import com.mr.myapplication.TestHackerApplication
import com.mr.myapplication.TestHackerComponent
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.home.HackerNewsListActivity
import com.mr.myapplication.ui.home.HomeNewsListFragment
import com.mr.myapplication.ui.home.getByTimeAgo
import com.mr.myapplication.ui.news.HackerNewsActivity
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull


@RunWith(AndroidJUnit4::class)
class HackerNewsListTest {

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
        onView(withText("Hacker News")).check(matches(isDisplayed()))
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
        onView(withId(R.id.loading_progress_bar)).check(matches(isDisplayed()))
        Thread.sleep(5000L)
        onView(withId(R.id.news_list_container)).check(matches(isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun testErrorScreen() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories()).thenReturn(Single.error(Exception("Mock Exception")))
        activityRule.launchActivity(null)
        Thread.sleep(2000L)
        onView(withId(R.id.retry_view_news_list)).check(matches(isDisplayed()))
        activityRule.finishActivity()
    }

    @Test
    fun testRetry() {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories()).thenReturn(Single.error(Exception("Mock Exception")))
        activityRule.launchActivity(null)
        Thread.sleep(2000L)
        //Test on error "Retry" screen is displayed
        onView(withId(R.id.retry_view_news_list)).check(matches(isDisplayed()))
        mockStoryData()
        //After click on retry
        onView(withId(R.id.retry)).perform(click())
        //List is displayed with news list
        onView(withId(R.id.loading_progress_bar)).check(matches(isDisplayed()))
        Thread.sleep(5000L)
        onView(withId(R.id.news_list_container)).check(matches(isDisplayed()))
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
        onView(withId(R.id.news_list_container)).check(matches(isDisplayed()))
        Thread.sleep(5000L)
        // First scroll to the position that needs to be matched and click on it.
        val actionOnItemAtPosition = actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
        onView(withId(R.id.news_list_recycler_view)).perform(actionOnItemAtPosition)

        //Then news activity should be opened
        intended(hasComponent(HackerNewsActivity::class.java.name))
        assert(Iterables.getOnlyElement(Intents.getIntents()).getBundleExtra("bundle").getParcelable<Story>("story") != null)
        intentActivityRule.finishActivity()
    }

    @Test
    fun testNewsInfoIsDisplayed() {
        val story = mockStoryData()

        activityRule.launchActivity(null)
        //Check if news list fragment is displayed
        val newsListFragment =
            activityRule.activity.supportFragmentManager.findFragmentById(R.id.home_fragment) as HomeNewsListFragment
        assertNotNull(newsListFragment)

        //List is displayed with news list
        Thread.sleep(3000L)
        onView(withId(R.id.news_list_container)).check(matches(isDisplayed()))
        Thread.sleep(5000L)
        // First scroll to the position that needs to be matched and click on it.
        val actionOnItemAtPosition = actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
        onView(withId(R.id.news_list_recycler_view)).perform(actionOnItemAtPosition)
        //Verify story information is displayed
        onView(withText("Sample story")).check(matches(isDisplayed()))

        val resources = activityRule.activity.resources
        val kidsSize: Int = story.kids?.size ?: 0

        val hours = Calendar.getInstance().getByTimeAgo(story.storyTime)
        //Meta info is displayed
        val metaString = resources.getString(
            R.string.description,
            story.score,
            resources.getQuantityString(R.plurals.points_plural, story.score),
            story.author,
            hours,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
        onView(withText(metaString)).check(matches(isDisplayed()))
        activityRule.finishActivity()
    }

    /**
     * Mock story data
     */
    private fun mockStoryData(): Story {
        val hackerNewsApi = (app.hackerComponent as TestHackerComponent).getHackerNewsApi()
        Mockito.`when`(hackerNewsApi.getTopStories())
            .thenReturn(Single.just(Arrays.asList(2000L)).delay(2, TimeUnit.SECONDS))
        val story =
            Story("by", "2000", Collections.emptyList(), 20, 123123L, "Sample story", "story", "https://www.google.com")
        Mockito.`when`(hackerNewsApi.getData("2000")).thenReturn(Single.just(story))
        return story
    }
}