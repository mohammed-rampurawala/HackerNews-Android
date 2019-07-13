package com.mr.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mr.myapplication.network.Comment
import com.mr.myapplication.network.IHackerNewsAPI
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.home.model.LoadingType
import com.mr.myapplication.ui.home.model.Resource
import com.mr.myapplication.ui.home.model.ResourceState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Hacker view model containing business logic for hacker news
 */
class HackerViewModel @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val hackerApi: IHackerNewsAPI
) :
    ViewModel() {

    companion object {
        const val MAX_STORIES_CHUNK_SIZE = 20L
    }

    /**
     * List of stories live data
     */
    private val mStoryLiveData: MutableLiveData<Resource<List<Story>>> = MutableLiveData()

    /**
     * List of stories live data
     */
    private val mCommentsLiveData: MutableLiveData<Resource<List<Comment>>> = MutableLiveData()

    /**
     * Live data to notify if there is more stories to load
     */
    private val mAddMoreItemLiveData: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Linked List of all the story ids received from the server. LinkedList because order of the ids need to be maintained.
     */
    private val listOfNewsId = LinkedList<Long>()

    /**
     * List of stories loaded
     */
    private val listOfStories = LinkedList<Story>()

    private val listOfComments = LinkedList<Comment>()

    private var commentDisposable = CompositeDisposable();

    /**
     * Get all the IDS from the server then only process HackerViewModel.MAX_STORIES_CHUNK_SIZE so that only few stories are fetched first. Then more stories later.
     */
    fun getStories() {
        if (mStoryLiveData.value != null && mStoryLiveData.value?.status == ResourceState.LOADING) {
            return
        }
        listOfStories.clear()
        mStoryLiveData.postValue(Resource(ResourceState.LOADING, null))
        compositeDisposable.add(
            hackerApi.getTopStories()
                .doOnSuccess { listOfNewsId.addAll(it) }
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .fetchStoriesFromObservable()
                .toList()
                .subscribe(getListOfStoriesObserver(), getThrowable()))
    }

    /**
     * Get the id from the listOfNewsId and fetch stories from server
     */
    fun getMoreStories() {
        mStoryLiveData.postValue(Resource(ResourceState.MORE_LOADING, null))
        val subList = LinkedList<Long>()
        subList.addAll(listOfNewsId.subList(0, listOfNewsId.size))
        compositeDisposable.add(
            Observable.fromIterable(subList)
                .fetchStoriesFromObservable()
                .toList()
                .subscribe(getListOfStoriesObserver(), getThrowable())
        )
    }

    /**
     * Load comments for a story from server
     */
    fun getStoryComment(story: Story) {
        commentDisposable.dispose()

        commentDisposable = CompositeDisposable()
        commentDisposable.add(
            Observable.fromIterable(story.kids)
                .flatMap { hackerApi.getComment(it).toObservable() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("Comment Loaded::${it.text}")
                    listOfComments.add(it)
                    mCommentsLiveData.postValue(Resource(ResourceState.SUCCESS, listOfComments))
                }, {
                    Timber.e(it, "Error in loading comments")
                })
        )
    }

    /**
     * Observer for list of stories
     */
    private fun getListOfStoriesObserver(): ((t: List<Story>) -> Unit)? {
        return {
            listOfStories.addAll(it)
            mStoryLiveData.postValue(Resource(ResourceState.SUCCESS, listOfStories))
            mAddMoreItemLiveData.postValue(!listOfNewsId.isEmpty())
        }
    }

    private fun getThrowable(): ((t: Throwable) -> Unit)? {
        return {
            Timber.e(it, "Error")
            mStoryLiveData.postValue(Resource(ResourceState.ERROR, null))
        }
    }

    /**
     * Extension function to fetch the stories from server with max chunk size
     */
    private fun <T> Observable<T>.fetchStoriesFromObservable(): Observable<Story> {
        return take(MAX_STORIES_CHUNK_SIZE)
            .getStoryFromServer(hackerApi)
            .doOnNext {
                listOfNewsId.remove(it.id.toLong())
                Timber.d("Story Fetched : ${it.storyTitle}")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Load story from server
     */
    private fun <T> Observable<T>.getStoryFromServer(hackerApi: IHackerNewsAPI): Observable<Story> {
        return concatMap { hackerApi.getData(it.toString()).toObservable() }
    }

    override fun onCleared() {
        super.onCleared()
        listOfNewsId.clear()
        compositeDisposable.dispose()
    }

    /**
     * To show more data
     */
    fun getAddMoreItemLiveData(): LiveData<Boolean> {
        return mAddMoreItemLiveData
    }

    /**
     * Live data to observe list of stories
     */
    fun getStoryListLiveData(): LiveData<Resource<List<Story>>> {
        return mStoryLiveData
    }

    fun getCommentsLiveData(): LiveData<Resource<List<Comment>>> {
        return mCommentsLiveData
    }

}
