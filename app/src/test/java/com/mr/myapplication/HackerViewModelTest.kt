package com.mr.myapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mr.myapplication.network.IHackerNewsAPI
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.HackerViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
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

        Mockito.`when`(hackerNewsAPI.getData("4999")).thenReturn(Single.just(mapOfStories[4999]))
        Mockito.`when`(hackerNewsAPI.getData("5000")).thenReturn(Single.just(mapOfStories[5000]))
        hackerViewModel.getStoryListLiveData().observeForever {
            for (story in it) {
                assert(mapOfStories[story.id.toLong()] == story)
            }
        }
        hackerViewModel.getStories()
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
        hackerViewModel.getStoryListLiveData().observeForever {
            for (story in it) {
                assert(mapOfStories[story.id.toLong()] == story)
            }
        }
        hackerViewModel.getAddMoreItemLiveData().observeForever {
            assert(!it)
        }
        hackerViewModel.getMoreStories()
    }
}