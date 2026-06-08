package com.flownews.api.user.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.common.api.CurrentUser
import com.flownews.api.user.app.UserDeviceTokenUpdateRequest
import com.flownews.api.user.app.UserUpdateService
import com.flownews.api.user.app.UserWithdrawRequest
import com.flownews.api.user.domain.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileUpdateApi(
    private val userUpdateService: UserUpdateService,
) {
    @PostMapping("/clientsvc/users/device-token")
    fun updateDeviceToken(
        @CurrentUser user: User,
        @RequestBody request: UserDeviceTokenUpdateRequest,
    ): ApiResponse<Void?> {
        val withUserId = request.withUserId(user.requireId())
        userUpdateService.updateDeviceToken(withUserId)

        return ApiResponse.ok()
    }

    @PostMapping("/clientsvc/users/withdraw")
    fun withdraw(
        @CurrentUser user: User,
        @RequestBody request: UserWithdrawRequest,
    ): ApiResponse<Void?> {
        userUpdateService.withdraw(user.requireId(), request)

        return ApiResponse.ok()
    }
}
