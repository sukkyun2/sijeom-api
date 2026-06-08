package com.flownews.api.event.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.common.app.NoDataException
import com.flownews.api.event.app.EventLikeService
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EventLikeApi(
    private val eventLikeService: EventLikeService,
) {
    @PostMapping("/clientsvc/events/{eventId}/like")
    fun toggleLike(
        @PathVariable eventId: Long,
        @CurrentUser user: User,
    ): ApiResponse<Void?> =
        try {
            eventLikeService.toggleLike(eventId, user)
            ApiResponse.ok()
        } catch (e: NoDataException) {
            ApiResponse.nodata()
        }
}
