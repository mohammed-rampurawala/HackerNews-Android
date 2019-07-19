package com.mr.myapplication.ui.news

import android.os.Bundle
import com.mr.myapplication.R
import com.mr.myapplication.network.Story
import dagger.android.support.DaggerAppCompatActivity

class HackerNewsActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity)
        //Don't refresh the view in case of config changes since story is being already loaded into the detail screen.
        if (savedInstanceState == null) {
            val newsFragment =
                supportFragmentManager.findFragmentById(R.id.story_detail_fragment) as NewsFragment
            intent.getBundleExtra(BUNDLE).getParcelable<Story>(STORY)?.let {
                newsFragment.refresh(it)
            }
        }
    }

    companion object {
        const val BUNDLE = "bundle"
        const val STORY = "story"
    }
}