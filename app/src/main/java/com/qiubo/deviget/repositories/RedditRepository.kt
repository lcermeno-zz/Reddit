package com.qiubo.deviget.repositories

import com.qiubo.deviget.api.ApiClient
import com.qiubo.deviget.api.GetTopRequest
import com.qiubo.deviget.api.ResultWrapper
import com.qiubo.deviget.events.GetTopEvent

interface IRedditRepository {

    suspend fun fetchTopEntries(request: GetTopRequest? = null): GetTopEvent
}

class RedditRepository : Repository(), IRedditRepository {
    override suspend fun fetchTopEntries(request: GetTopRequest?): GetTopEvent {
        return when (val response =
            safeApiCall { ApiClient.getRedditService().fetchTop(request?.after) }) {
            is ResultWrapper.Success -> GetTopEvent(response.value).apply { success = true }
            is ResultWrapper.GenericError -> GetTopEvent().apply {
                message = response.error?.message
            }
            is ResultWrapper.NetworkError -> GetTopEvent().apply { message = "Network Error" }
        }

    }
}

