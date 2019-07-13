package com.mr.myapplication.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mr.myapplication.R
import com.mr.myapplication.network.Story
import java.util.LinkedList

/**
 * Hacker news list adapter
 */
class HackerNewsAdapter(
    private val mLayoutInflater: LayoutInflater,
    private val mMoreClickListener: () -> Unit,
    private val mStoryClickListener: (story: Story) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * List of stories
     */
    private val mListOfStories = LinkedList<Story>()

    /**
     * True if there is "More" view to be shown in bottom of list to load more stories. False, If no more stories left
     * to load
     */
    private var mShowAddMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.more_item) {
            return MoreItemViewHolder(mLayoutInflater.inflate(viewType, parent, false), mMoreClickListener)
        }
        return HackerNewsItemViewHolder(mLayoutInflater.inflate(viewType, parent, false), mStoryClickListener)
    }

    override fun getItemCount(): Int {
        return if (mShowAddMore) {
            mListOfStories.size + 1
        } else {
            mListOfStories.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == R.layout.news_item) {
            (holder as HackerNewsItemViewHolder).bind(mListOfStories[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mListOfStories.size) {
            R.layout.more_item
        } else {
            R.layout.news_item
        }
    }

    /**
     * Add the stories to the current list of stories
     */
    fun addStories(listOfStories: List<Story>?) {
        listOfStories?.let {
            mListOfStories.clear()
            mListOfStories.addAll(it)
        }
    }

    /**
     * Flag to identify that we need to show "More" view at the bottom of stories list
     */
    fun showAddMore(boolean: Boolean) {
        mShowAddMore = boolean
    }
}