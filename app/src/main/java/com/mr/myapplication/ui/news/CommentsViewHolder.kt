package com.mr.myapplication.ui.news

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mr.myapplication.R
import com.mr.myapplication.network.Comment
import com.mr.myapplication.ui.home.getByTimeAgo
import timber.log.Timber
import java.util.*

/**
 * Comments view holder to be displayed on news description view
 */
class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mCommentsTextView: TextView = itemView.findViewById(R.id.comment_text_view)

    private val mAuthorTextView: TextView = itemView.findViewById(R.id.author_text_view)

    private val mCurrentCalendar = Calendar.getInstance()

    /**
     * Bind the comments on view
     */
    fun bind(comment: Comment) {
        if (!comment.isDeleted) {
            Timber.d("Binding Comment: %s", comment.text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCommentsTextView.text = Html.fromHtml(comment.text, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mCommentsTextView.text = Html.fromHtml(comment.text)
            }

            mAuthorTextView.text = String.format("%s %s", comment.author, mCurrentCalendar.getByTimeAgo(comment.time))
        } else {
            Timber.d("Binding Comment: %s", "Comment is Deleted")
            mCommentsTextView.text = mAuthorTextView.context.getString(R.string.comment_is_deleted)
            mAuthorTextView.text = ""
        }
    }
}