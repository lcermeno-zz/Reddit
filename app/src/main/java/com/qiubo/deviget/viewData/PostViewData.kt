package com.qiubo.deviget.viewData

import com.qiubo.deviget.api.RedditChild

data class PostViewData(
    val title: String,
    val author: String,
    val date: Long,
    val thumbnailUrl: String,
    val commentQty: Int,
    val seen: Boolean = false
)

fun RedditChild.toViewData() = PostViewData(data.title, data.author, data.date, data.thumbnailUrl, data.commentQty)