package com.flownews.api.interaction.infra

import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class UserProfileApiClientFallbackFactory : FallbackFactory<UserProfileApiClient> {
    private val logger = LoggerFactory.getLogger(UserProfileApiClientFallbackFactory::class.java)

    override fun create(cause: Throwable): UserProfileApiClient =
        object : UserProfileApiClient {
            override fun updateProfileByTopic(
                userId: Long,
                request: TopicBasedProfileUpdateRequest,
            ): UserProfileUpdateResponse {
                logger.warn("토픽 프로필 업데이트 실패 userId={}: {}", userId, cause.message)
                return UserProfileUpdateResponse(status = "FALLBACK", userId = userId.toInt(), processedCount = 0)
            }

            override fun updateProfileByEvent(
                userId: Long,
                request: EventBasedProfileUpdateRequest,
            ): UserProfileUpdateResponse {
                logger.warn("이벤트 프로필 업데이트 실패 userId={}: {}", userId, cause.message)
                return UserProfileUpdateResponse(status = "FALLBACK", userId = userId.toInt(), processedCount = 0)
            }
        }
}
