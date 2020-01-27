package com.qiubo.deviget.api

import com.google.gson.annotations.SerializedName


// requests
data class GetTopRequest(val after: String)

// responses
data class ErrorResponse(val message: String)

data class GetTopResponse(val data: RedditData)

data class RedditData(val children: List<RedditChild>, val after: String)

data class RedditChild(val data: RedditChildData)

data class RedditChildData(
    @SerializedName("id")
    val id: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("created_utc")
    val date: Long,
    @SerializedName("thumbnail")
    val thumbnailUrl: String,
    @SerializedName("num_comments")
    val commentQty: Int
)