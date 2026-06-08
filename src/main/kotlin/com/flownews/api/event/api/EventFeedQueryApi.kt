package com.flownews.api.event.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.event.app.EventFeedQueryResponse
import com.flownews.api.event.app.EventFeedQueryService
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class EventFeedQueryApi(
    private val eventFeedQueryService: EventFeedQueryService,
) {
    @GetMapping("/clientsvc/events/feed")
    fun getUserEventFeed(
        @CurrentUser user: User?,
        @RequestParam(required = false) category: String?,
    ): ApiResponse<List<EventFeedQueryResponse>> {
        val eventFeed = eventFeedQueryService.getEventFeeds(user, category)
        return ApiResponse.ok(eventFeed)
    }
}
