package com.mr.myapplication.network

import com.google.gson.annotations.SerializedName

/**
 * Comment data class for comments on story
 */
data class Comment(
    @SerializedName("by") var author: String, @SerializedName("id") var id: String,
    @SerializedName("parent") var parent: String, @SerializedName("text") var text: String?,
    @SerializedName("type") var type: String, @SerializedName("time") var time: Long,
    @SerializedName("deleted") var isDeleted: Boolean = false
)