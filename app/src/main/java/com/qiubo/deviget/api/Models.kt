package com.qiubo.deviget.api

// responses
data class ErrorResponse(val message: String)
data class GetTopResponse(val error: String)

// requests
data class GetTopRequest(val after: String)