package com.flownews.api.user.app

import com.flownews.api.common.app.NoDataException
import com.flownews.api.user.domain.User
import com.flownews.api.user.domain.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserUpdateService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun updateDeviceToken(request: UserDeviceTokenUpdateRequest): User {
        val (userId, deviceToken) = request
        val user = getUser(userId)
        user.updateDeviceToken(deviceToken)

        return userRepository.save(user)
    }

    @Transactional
    fun withdraw(
        userId: Long,
        request: UserWithdrawRequest,
    ): User {
        val user = getUser(userId)

        user.withdraw(request.reason)

        return userRepository.save(user)
    }

    private fun getUser(userId: Long): User =
        userRepository.findById(userId).orElseThrow {
            NoDataException("user not found: $userId")
        }
}
