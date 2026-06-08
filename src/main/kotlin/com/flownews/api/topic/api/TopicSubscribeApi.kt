package com.flownews.api.topic.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.common.app.NoDataException
import com.flownews.api.topic.app.TopicSubscribeRequest
import com.flownews.api.topic.app.TopicSubscribeService
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TopicSubscribeApi(
    private val topicSubscribeService: TopicSubscribeService,
) {
    @PostMapping("/clientsvc/topics/{topicId}/toggle-subscription")
    fun toggleSubscription(
        @PathVariable topicId: Long,
        @CurrentUser user: User,
    ): ApiResponse<out Any?> =
        try {
            val req =
                TopicSubscribeRequest(
                    user = user,
                    topicId = topicId,
                )
            topicSubscribeService.toggleSubscription(req)
            ApiResponse.ok()
        } catch (e: NoDataException) {
            ApiResponse.nodata()
        }
}
