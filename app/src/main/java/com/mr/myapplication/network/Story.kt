package com.mr.myapplication.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("by") var author: String,
    @SerializedName("id") var id: String,
    @SerializedName("kids") var kids: List<String>?,
    @SerializedName("score") var score: Int,
    @SerializedName("time") var storyTime: Long,
    @SerializedName("title") var storyTitle: String,
    @SerializedName("type") var storyType: String,
    @SerializedName("url") var storyUrl: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(id)
        parcel.writeStringList(kids)
        parcel.writeInt(score)
        parcel.writeLong(storyTime)
        parcel.writeString(storyTitle)
        parcel.writeString(storyType)
        parcel.writeString(storyUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }
}