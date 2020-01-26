package com.qiubo.deviget.events

import com.qiubo.deviget.api.GetTopResponse

open class BaseEvent {
    var success: Boolean = false
    var message: String? = null
}

data class GetTopEvent(val getTopResponse: GetTopResponse? = null) : BaseEvent()