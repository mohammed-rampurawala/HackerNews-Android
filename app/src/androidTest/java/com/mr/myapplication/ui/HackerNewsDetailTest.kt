package com.mr.myapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.mr.myapplication.R
import com.mr.myapplication.TestHackerApplication
import com.mr.myapplication.network.Comment
import com.mr.myapplication.network.Story
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
        onView(withId(R.id.story_title_text_view)).check(matches(withText(getMockedStoryWithoutComments().storyTitle)))
        val kidsSize: Int = getMockedStoryWithoutComments().kids?.size ?: 0

        val hours = Calendar.getInstance().getByTimeAgo(getMockedStoryWithoutComments().storyTime)
        val resources = newsActivityRule.activity.resources
        val description = resources.getString(
            R.string.description,
            getMockedStoryWithoutComments().score,
            resources.getQuantityString(R.plurals.points_plural, getMockedStoryWithoutComments().score),
            getMockedStoryWithoutComments().author,
            hours,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
        onView(withId(R.id.story_description_text_view)).check(matches(withText(description)))
        onView(withId(R.id.no_comments_textview)).check(matches(isDisplayed()))
        newsActivityRule.finishActivity()
    }

    @Test
    fun testComments() {
        val mockedComment = getMockedComment()

        //Mock the zeroth comment id and Delay to verify that loading comment is displayed
        Mockito.`when`(app.hackerNewsAPI.getComment(getCommentsList()[0]))
            .thenReturn(Single.just(mockedComment).delay(2, TimeUnit.SECONDS))
        launchNewsActivityWithComments()

        val mockedStoryWithComments = getMockedStoryWithComments()

        //when news detail activity is launched then Story title is displayed
        onView(withId(R.id.story_title_text_view)).check(matches(withText(mockedStoryWithComments.storyTitle)))
        val kidsSize: Int = mockedStoryWithComments.kids?.size ?: 0

        val hours = Calendar.getInstance().getByTimeAgo(mockedStoryWithComments.storyTime)
        val resources = newsActivityRule.activity.resources
        val description = resources.getString(
            R.string.description,
            mockedStoryWithComments.score,
            resources.getQuantityString(R.plurals.points_plural, mockedStoryWithComments.score),
            mockedStoryWithComments.author,
            hours,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
        //Then story description is displayed
        onView(withId(R.id.story_description_text_view)).check(matches(withText(description)))

        //When comments are loading then loading comments view should be displayed
        onView(withId(R.id.comments_loading_container)).check(matches(isDisplayed()))
        Thread.sleep(2000L)
        //On successful comment loading, then comment list container should be displayed
        onView(withId(R.id.comments_list_container)).check(matches(isDisplayed()))
        onView(withId(R.id.comments_header)).check(matches(isDisplayed()))

        // First scroll to the position that needs to be matched and click on it.
        val actionOnItemAtPosition =
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click())
        onView(withId(R.id.comments_recycler_view)).perform(actionOnItemAtPosition)
        //Verify story information is displayed
        onView(withText(mockedComment.text)).check(matches(isDisplayed()))
        onView(withText(String.format("%s %s", mockedComment.author, Calendar.getInstance().getByTimeAgo(mockedComment.time)))).check(matches(isDisplayed()))
        newsActivityRule.finishActivity()
    }

    private fun launchNewsActivityWithComments() {
        val intent = Intent(app, HackerNewsActivity::class.java)
        val bundle = Bundle()
        val mockedStory = getMockedStoryWithComments()
        bundle.putParcelable("story", mockedStory)
        intent.putExtra("bundle", bundle)
        newsActivityRule.launchActivity(intent)
    }

    /**
     * Launch Hacker news detail activity with mocked story data stored in the intent
     */
    private fun launchNewsActivity() {
        val intent = Intent(app, HackerNewsActivity::class.java)
        val bundle = Bundle()
        val mockedStory = getMockedStoryWithoutComments()
        bundle.putParcelable("story", mockedStory)
        intent.putExtra("bundle", bundle)
        newsActivityRule.launchActivity(intent)
    }

    /**
     * Create the mocked story
     */
    private fun getMockedStoryWithoutComments(): Story {
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

    private fun getMockedStoryWithComments(): Story {
        return Story(
            "by",
            "2000",
            getCommentsList(),
            20,
            123123L,
            "Sample story",
            "story",
            "https://www.google.com"
        )
    }

    private fun getCommentsList(): List<String> {
        return Arrays.asList("20001")
    }

    private fun getMockedComment(): Comment {
        return Comment("John", "20001", "2000", "Hello World", "comment", 123423L)
    }

}