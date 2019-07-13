package com.mr.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import com.mr.myapplication.R
import com.mr.myapplication.network.Story
import com.mr.myapplication.ui.news.NewsActivity
import com.mr.myapplication.ui.news.NewsFragment
import dagger.android.support.DaggerAppCompatActivity

/**
 * Main activity displaying the hacker news
 */
class HackerNewsListActivity : DaggerAppCompatActivity(), StorySelectionListener {

    private var mIsTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_news)
        mIsTwoPane = supportFragmentManager.findFragmentById(R.id.story_detail_fragment) != null
    }

    override fun onStorySelected(story: Story) {
        if (mIsTwoPane) {
            (supportFragmentManager.findFragmentById(R.id.story_detail_fragment) as NewsFragment).refresh(story)
        } else {
            val intent = Intent(this, NewsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(NewsActivity.STORY, story)
            intent.putExtra(NewsActivity.BUNDLE, bundle)
            startActivity(intent)
        }
    }
}

interface StorySelectionListener {
    fun onStorySelected(story: Story)
}