package com.flownews.api.user.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.user.app.UserQueryResponse
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserQueryApi {
    @GetMapping("/clientsvc/users/me")
    fun getCurrentUser(
        @CurrentUser user: User,
    ): ApiResponse<UserQueryResponse> {
        val response = UserQueryResponse.from(user)
        return ApiResponse.ok(response)
    }
}
