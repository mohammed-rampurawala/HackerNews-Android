package com.mr.myapplication.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MoreItemViewHolder(view: View, moreClickListener: () -> Unit) : RecyclerView.ViewHolder(view) {
    init {
        view.setOnClickListener {
            moreClickListener.invoke()
        }
    }
}