package com.flownews.api.push.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.app.NoDataException
import com.flownews.api.push.app.PushMessageSendRequest
import com.flownews.api.push.app.TopicEventPushService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PushMessageSendApi(
    private val pushMessageSender: TopicEventPushService,
) {
    @PostMapping("/intsvc/notifications/push", params = ["by=topic"])
    fun sendPushMessageByTopic(
        @RequestBody req: PushMessageSendRequest,
    ): ApiResponse<out Any?> =
        try {
            ApiResponse.ok(pushMessageSender.sendPushMessages(req.requireTopicId()))
        } catch (e: NoDataException) {
            ApiResponse.nodata()
        }
}
