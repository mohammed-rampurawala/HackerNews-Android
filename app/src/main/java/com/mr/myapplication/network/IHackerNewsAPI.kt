package com.mr.myapplication.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IHackerNewsAPI {

    @GET("v0/topstories.json")
    fun getTopStories(): Single<List<Long>>

    @GET("/v0/item/{article_id}.json")
    fun getData(@Path("article_id") id: String): Single<Story>

    @GET("/v0/item/{comment_id}.json")
    fun getComment(@Path("comment_id") id: String): Single<Comment>
}