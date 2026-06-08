package com.flownews.api.interaction.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.interaction.app.InteractionRecordRequest
import com.flownews.api.interaction.app.InteractionRecordService
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class InteractionRecordApi(
    private val interactionRecordService: InteractionRecordService,
) {
    @PostMapping("/clientsvc/interactions")
    fun recordInteraction(
        @RequestBody request: InteractionRecordRequest,
        @CurrentUser user: User,
    ): ApiResponse<Void?> {
        interactionRecordService.recordInteraction(request, user)

        return ApiResponse.ok()
    }
}
