package com.flownews.api.topic.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.common.app.NoDataException
import com.flownews.api.topic.app.TopicTimelineQueryService
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TopicTimelineQueryApi(
    private val topicTimelineQueryService: TopicTimelineQueryService,
) {
    @GetMapping("/clientsvc/topics/{topicId}")
    fun getTopic(
        @PathVariable topicId: Long,
        @CurrentUser user: User?,
    ): ApiResponse<out Any?> =
        try {
            ApiResponse.ok(topicTimelineQueryService.getTopic(user, topicId))
        } catch (e: NoDataException) {
            ApiResponse.nodata()
        }
}
