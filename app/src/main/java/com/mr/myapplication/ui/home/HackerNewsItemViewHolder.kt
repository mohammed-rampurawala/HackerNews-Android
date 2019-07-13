package com.mr.myapplication.ui.home

import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mr.myapplication.R
import com.mr.myapplication.network.Story
import java.util.Calendar

/**
 * Hacker news view holder
 */
class HackerNewsItemViewHolder(itemView: View, private val storyClickListener: (story: Story) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    /**
     * Story title text view
     */
    private var mStoryTitleTextView: TextView = itemView.findViewById(R.id.story_title)

    /**
     * Story description text view
     */
    private var mDescriptionTextView: TextView = itemView.findViewById(R.id.description_text_view)

    /**
     * Current date to fetch the hours
     */
    private var mCurrentCalendar = Calendar.getInstance()

    /**
     * Bind the news story to the UI
     */
    fun bind(story: Story) {
        mStoryTitleTextView.text = story.storyTitle
        itemView.setOnClickListener { storyClickListener.invoke(story) }
        val resources = itemView.context.resources;

        val kidsSize: Int = story.kids?.size ?: 0

        val hours = mCurrentCalendar.getByTimeAgo(story.storyTime);

        mDescriptionTextView.text = resources.getString(
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

/**
 * Calendar extension function to get the hours from story time
 */
fun Calendar.getByTimeAgo(storyTimeInSeconds: Long): String {
    return DateUtils.getRelativeTimeSpanString(storyTimeInSeconds * 1000, System.currentTimeMillis(), 0).toString()
}