package com.flownews.api.interaction.infra

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "user-profile-service",
    url = "\${recommendation.api.url}",
    fallbackFactory = UserProfileApiClientFallbackFactory::class,
)
interface UserProfileApiClient {
    @PostMapping("/v1/users/{userId}/profile/topics")
    fun updateProfileByTopic(
        @PathVariable userId: Long,
        @RequestBody request: TopicBasedProfileUpdateRequest,
    ): UserProfileUpdateResponse

    @PostMapping("/v1/users/{userId}/profile/events")
    fun updateProfileByEvent(
        @PathVariable userId: Long,
        @RequestBody request: EventBasedProfileUpdateRequest,
    ): UserProfileUpdateResponse
}
