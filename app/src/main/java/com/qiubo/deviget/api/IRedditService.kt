package com.qiubo.deviget.api

import retrofit2.http.GET
import retrofit2.http.Query

interface IRedditService {
    @GET("top.json")
    suspend fun fetchTop(@Query("after") after: String? = null): GetTopResponse
}