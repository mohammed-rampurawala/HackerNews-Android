package com.mr.myapplication.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mr.myapplication.R
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.HackerViewModel
import com.mr.myapplication.ui.home.model.ResourceState
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject

/**
 * Home fragment to display the top hacker news list
 */
class HomeNewsListFragment : DaggerFragment() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    /**
     * Callback for to be implemented by Activity to switch fragments or start an activity in case of tablet and phone
     */
    private lateinit var mStorySelectionListener: StorySelectionListener

    /**
     * Hacker news adapter to be set onto recycler view
     */
    private lateinit var mHackerNewsAdapter: HackerNewsAdapter

    /**
     * Hacker view model for business logic
     */
    private lateinit var mHackerViewModel: HackerViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        retainInstance = true
        if (context is StorySelectionListener) {
            mStorySelectionListener = context
        } else {
            throw RuntimeException("Unable cast context to ${StorySelectionListener::class.java.simpleName}")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHackerViewModel = ViewModelProviders.of(this, mViewModelFactory)[HackerViewModel::class.java]
        initRecyclerView()
        initSwipeRefreshLayout()
        //If bundle is not null that means there was a config change therefore don't call API
        if (savedInstanceState == null) {
            mHackerViewModel.getStories()
        }
        retry.setOnClickListener { mHackerViewModel.getStories() }
    }

    /**
     * Initialize the swipe refresh layout
     */
    private fun initSwipeRefreshLayout() {
        news_list_container.setOnRefreshListener {
            mHackerViewModel.getStories()
        }
    }

    /**
     * Initialize the hacker news recycler view
     */
    private fun initRecyclerView() {
        mHackerViewModel.getStoryListLiveData().observe(this, Observer {
            when (it.status) {
                ResourceState.LOADING -> showLoading()
                ResourceState.SUCCESS -> showNewsList(it.data!!)
                ResourceState.ERROR -> showError()
                ResourceState.MORE_LOADING -> news_list_container.isRefreshing = true
            }
        })

        mHackerViewModel.getAddMoreItemLiveData().observe(this, Observer {
            mHackerNewsAdapter.showAddMore(it)
            mHackerNewsAdapter.notifyItemChanged(mHackerNewsAdapter.itemCount)
        })

        mHackerNewsAdapter =
            HackerNewsAdapter(LayoutInflater.from(this.activity), getMoreItemClick(), getStorySelectionFunction())
        news_list_recycler_view.adapter = mHackerNewsAdapter

        val layoutManager = LinearLayoutManager(this.activity, RecyclerView.VERTICAL, false)
        news_list_recycler_view.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this.activity, layoutManager.orientation)
        news_list_recycler_view.addItemDecoration(dividerItemDecoration)
    }

    /**
     * Return the function for story selected on recycler view
     */
    private fun getStorySelectionFunction(): (story: Story) -> Unit {
        return {
            mStorySelectionListener.onStorySelected(it)
        }
    }

    /**
     * Return the function for more item click
     */
    private fun getMoreItemClick(): () -> Unit {
        return {
            mHackerViewModel.getMoreStories()
        }
    }

    /**
     * Display error accordingly if no list was fetched then display error screen otherwise show toast message
     */
    private fun showError() {
        news_list_container.isRefreshing = false
        if (news_list_recycler_view.adapter!!.itemCount == 0) {
            retry_view_news_list.visibility = View.VISIBLE
            loading_progress_bar.visibility = View.GONE
            news_list_container.visibility = View.GONE
        } else {
            Toast.makeText(activity, getString(R.string.error_text), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Display news list stories on UI
     */
    private fun showNewsList(stories: List<Story>) {
        //Hide Loading if its showing else do nothing
        news_list_container.isRefreshing = false
        if (news_list_container.visibility != View.VISIBLE) {
            loading_progress_bar.visibility = View.GONE
            news_list_container.visibility = View.VISIBLE
            retry_view_news_list.visibility = View.GONE
        }

        //Refresh the Data
        val startCount = mHackerNewsAdapter.itemCount
        mHackerNewsAdapter.addStories(stories)
        val endCount = mHackerNewsAdapter.itemCount
        mHackerNewsAdapter.notifyItemRangeChanged(startCount, endCount)
    }

    /**
     * Show the loading view
     */
    private fun showLoading() {
        loading_progress_bar.visibility = View.VISIBLE
        news_list_container.visibility = View.GONE
        retry_view_news_list.visibility = View.GONE
        news_list_container.isRefreshing = true
    }
}