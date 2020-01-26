package com.qiubo.deviget.usecases

import com.qiubo.deviget.api.GetTopRequest
import com.qiubo.deviget.events.GetTopEvent
import com.qiubo.deviget.repositories.IRedditRepository

class GetTopUseCase(private val repository: IRedditRepository){
    suspend operator fun invoke(request: GetTopRequest? = null): GetTopEvent = repository.fetchTopEntries(request)
}