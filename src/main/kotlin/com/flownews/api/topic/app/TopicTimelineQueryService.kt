package com.flownews.api.topic.app

import com.flownews.api.event.app.EventQueryService
import com.flownews.api.topic.domain.TopicQueryService
import com.flownews.api.user.domain.User
import org.springframework.stereotype.Service

@Service
class TopicTimelineQueryService(
    private val topicQueryService: TopicQueryService,
    private val eventQueryService: EventQueryService,
) {
    fun getTopic(
        user: User?,
        topicId: Long,
    ): TopicTimelineQueryResponse {
        val followedTopic = topicQueryService.getFollowedTopic(user, topicId)
        val likedEvents =
            followedTopic.topic.getEvents().map {
                eventQueryService.getLikedEvent(it.requireId(), user)
            }

        return TopicTimelineQueryResponse.of(
            followedTopic = followedTopic,
            events = likedEvents,
        )
    }
}
