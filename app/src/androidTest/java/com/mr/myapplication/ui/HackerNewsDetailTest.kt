package com.mr.myapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.mr.myapplication.R
import com.mr.myapplication.TestHackerApplication
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.home.getByTimeAgo
import com.mr.myapplication.ui.news.HackerNewsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class HackerNewsDetailTest {

    private lateinit var app: TestHackerApplication

    @get:Rule
    val newsActivityRule = ActivityTestRule<HackerNewsActivity>(HackerNewsActivity::class.java, false, false)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestHackerApplication
    }

    @Test
    fun testNewsListTitle() {
        launchNewsActivity()
        onView(withText("Hacker News")).check(matches(isDisplayed()))
        newsActivityRule.finishActivity()
    }

    @Test
    fun testStoryDetailsIsDisplayed() {
        launchNewsActivity()
        onView(withId(R.id.story_title_text_view)).check(matches(withText(getMockedStory().storyTitle)))
        val kidsSize: Int = getMockedStory().kids?.size ?: 0

        val hours = Calendar.getInstance().getByTimeAgo(getMockedStory().storyTime)
        val resources = newsActivityRule.activity.resources
        val description = resources.getString(
            R.string.description,
            getMockedStory().score,
            resources.getQuantityString(R.plurals.points_plural, getMockedStory().score),
            getMockedStory().author,
            hours,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
        onView(withId(R.id.story_description_text_view)).check(matches(withText(description)))
        onView(withId(R.id.no_comments_textview)).check(matches(isDisplayed()))
        newsActivityRule.finishActivity()
    }


    /**
     * Launch Hacker news detail activity with mocked story data stored in the intent
     */
    private fun launchNewsActivity() {
        val intent = Intent(app, HackerNewsActivity::class.java)
        val bundle = Bundle()
        val mockedStory = getMockedStory()
        bundle.putParcelable("story", mockedStory)
        intent.putExtra("bundle", bundle)
        newsActivityRule.launchActivity(intent)
    }

    /**
     * Create the mocked story
     */
    fun getMockedStory(): Story {
        return Story(
            "by",
            "2000",
            Collections.emptyList(),
            20,
            123123L,
            "Sample story",
            "story",
            "https://www.google.com"
        )
    }
}