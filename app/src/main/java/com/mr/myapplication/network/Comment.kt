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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (author != other.author) return false
        if (id != other.id) return false
        if (parent != other.parent) return false
        if (text != other.text) return false
        if (type != other.type) return false
        if (time != other.time) return false
        if (isDeleted != other.isDeleted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = author.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + isDeleted.hashCode()
        return result
    }
}