package com.mr.myapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mr.myapplication.network.Comment
import com.mr.myapplication.network.IHackerNewsAPI
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.HackerViewModel
import com.mr.myapplication.ui.home.model.ResourceState
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Exception
import java.util.*

class HackerViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var hackerNewsAPI: IHackerNewsAPI

    lateinit var compositeDisposable: CompositeDisposable

    lateinit var hackerViewModel: HackerViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        compositeDisposable = CompositeDisposable()
        hackerViewModel = HackerViewModel(compositeDisposable, hackerNewsAPI)
    }

    @Test
    fun testGetStories() {
        val listOfIds = mutableListOf<Long>()
        listOfIds.add(4999L)
        listOfIds.add(5000L)
        Mockito.`when`(hackerNewsAPI.getTopStories()).thenReturn(Single.just(listOfIds))

        val mapOfStories = mutableMapOf<Long, Story>()
        mapOfStories[4999L] = Story(
            "mohammed",
            "4999",
            Collections.emptyList(),
            12,
            213123,
            "Sample",
            "sample",
            "https://www.google.com"
        )
        mapOfStories[5000L] = Story(
            "mohammed rampurawala",
            "5000",
            Collections.emptyList(),
            12,
            213124,
            "Sample",
            "sample",
            "https://www.google.com"
        )
        var loadingShown = false
        Mockito.`when`(hackerNewsAPI.getData("4999")).thenReturn(Single.just(mapOfStories[4999]))
        Mockito.`when`(hackerNewsAPI.getData("5000")).thenReturn(Single.just(mapOfStories[5000]))
        hackerViewModel.getStoryListLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assertEquals(ResourceState.SUCCESS, it.status)
                for (story in it.data!!) {
                    assert(mapOfStories[story.id.toLong()] == story)
                }
            }
        }
        hackerViewModel.getStories()
        assert(loadingShown)
    }

    @Test
    fun testGetMoreStories() {
        val listOfIds = mutableListOf<Long>()
        listOfIds.add(4999L)
        listOfIds.add(5000L)
        Mockito.`when`(hackerNewsAPI.getTopStories()).thenReturn(Single.just(listOfIds))

        val mapOfStories = mutableMapOf<Long, Story>()
        mapOfStories[4999L] = Story(
            "mohammed",
            "4999",
            Collections.emptyList(),
            12,
            213123,
            "Sample",
            "sample",
            "https://www.google.com"
        )
        mapOfStories[5000L] = Story(
            "mohammed rampurawala",
            "5000",
            Collections.emptyList(),
            12,
            213124,
            "Sample",
            "sample",
            "https://www.google.com"
        )

        Mockito.`when`(hackerNewsAPI.getData("4999")).thenReturn(Single.just(mapOfStories[4999]))
        Mockito.`when`(hackerNewsAPI.getData("5000")).thenReturn(Single.just(mapOfStories[5000]))

        var loadingShown = false
        hackerViewModel.getStoryListLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.MORE_LOADING, it.status)
                loadingShown = true
            } else {
                assertEquals(ResourceState.SUCCESS, it.status)
                for (story in it.data!!) {
                    assert(mapOfStories[story.id.toLong()] == story)
                }
            }
        }
        hackerViewModel.getAddMoreItemLiveData().observeForever {
            assert(!it)
        }
        hackerViewModel.getMoreStories()
        assert(loadingShown)
    }

    @Test
    fun testLoadingComments() {
        val story = Story(
            "mohammed",
            "4999",
            Arrays.asList("8000"),
            12,
            213123,
            "Sample",
            "sample",
            "https://www.google.com"
        )
        val comment = Comment("by", "8000", "asdad", "asdasdasd", "asdasda", 123124L, false)
        Mockito.`when`(hackerNewsAPI.getComment("8000")).thenReturn(Single.just(comment))

        var loadingShown = false
        hackerViewModel.getCommentsLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assert(it.status == ResourceState.SUCCESS)
                assert(it.data?.size == 1)
                assert(it.data?.get(0) == comment)
            }
        }
        hackerViewModel.getStoryComment(story)
    }

    @Test
    fun testLoadingCommentError() {
        val story = Story(
            "mohammed",
            "4999",
            Arrays.asList("8000"),
            12,
            213123,
            "Sample",
            "sample",
            "https://www.google.com"
        )
        Mockito.`when`(hackerNewsAPI.getComment("8000")).thenReturn(Single.error(Exception("Runitime exception")))
        var loadingShown = false
        hackerViewModel.getCommentsLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assert(it.status == ResourceState.ERROR)
            }
        }
        hackerViewModel.getStoryComment(story)
    }
}