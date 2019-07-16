package com.mr.myapplication.ui.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mr.myapplication.R
import com.mr.myapplication.network.Comment
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.HackerViewModel
import com.mr.myapplication.ui.home.getByTimeAgo
import com.mr.myapplication.ui.home.model.ResourceState
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.news_fragment.*
import java.util.*
import javax.inject.Inject

class NewsFragment : DaggerFragment() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var hackerViewModel: HackerViewModel

    private lateinit var mCommentsAdapter: CommentsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hackerViewModel = ViewModelProviders.of(this, vmFactory)[HackerViewModel::class.java]
        initCommentsRecyclerView()
        hackerViewModel.getCommentsLiveData().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                ResourceState.LOADING -> showLoading()
                ResourceState.SUCCESS -> showComments(it.data)
                ResourceState.ERROR -> showErrorScreen()
                ResourceState.HIDE_LOADING -> hideLoading()
                else -> hideCommentsContainer()
            }
        })
    }

    private fun hideLoading() {
        if (mCommentsAdapter.itemCount == 0) {
            no_comments_textview.visibility = View.VISIBLE
        }
        comments_loading_container.visibility = View.GONE
    }

    private fun showErrorScreen() {
        if (mCommentsAdapter.itemCount > 0) {
            hideCommentsContainer()
        }
    }

    /**
     * Show comments list
     */
    private fun showComments(data: List<Comment>?) {
        mCommentsAdapter.setListOfComments(data!!)

        if (comments_list_container.visibility != View.VISIBLE) {
            comments_list_container.visibility = View.VISIBLE
        }
    }

    /**
     * Show loading screen
     */
    private fun showLoading() {
        comments_list_container.visibility = View.GONE
        comments_loading_container.visibility = View.VISIBLE
        no_comments_textview.visibility = View.GONE
    }

    /**
     * Hide comments container and show no comments view
     */
    private fun hideCommentsContainer() {
        comments_list_container.visibility = View.GONE
        comments_loading_container.visibility = View.GONE
        no_comments_textview.visibility = View.VISIBLE
    }

    /**
     * Initialize comments recycler view
     */
    private fun initCommentsRecyclerView() {
        mCommentsAdapter = CommentsAdapter(LayoutInflater.from(this.activity))
        val layoutManager = LinearLayoutManager(this.activity, RecyclerView.VERTICAL, false)
        comments_recycler_view.adapter = mCommentsAdapter
        comments_recycler_view.layoutManager = layoutManager
        comments_recycler_view.isNestedScrollingEnabled = false
    }

    /**
     * Refresh the comments view
     */
    fun refresh(story: Story) {
        //Load the comments
        hackerViewModel.getStoryComment(story)
        updateViews(story)
    }

    private fun updateViews(story: Story) {
        story_title_text_view.text = story.storyTitle

        val kidsSize: Int = story.kids?.size ?: 0

        val hours = Calendar.getInstance().getByTimeAgo(story.storyTime)

        story_description_text_view.text = resources.getString(
            R.string.description,
            story.score,
            resources.getQuantityString(R.plurals.points_plural, story.score),
            story.author,
            hours,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
    }
}