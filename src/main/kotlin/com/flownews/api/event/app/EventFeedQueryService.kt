package com.flownews.api.event.app

import com.flownews.api.event.domain.Event
import com.flownews.api.event.infra.EventRecommendationQueryService
import com.flownews.api.interaction.domain.InteractionRepository
import com.flownews.api.topic.app.TopicListQueryService
import com.flownews.api.topic.domain.TopicSubscriptionRepository
import com.flownews.api.user.domain.User
import org.springframework.stereotype.Service

@Service
class EventFeedQueryService(
    private val topicListQueryService: TopicListQueryService,
    private val topicSubscriptionRepository: TopicSubscriptionRepository,
    private val interactionRepository: InteractionRepository,
    private val eventRecommendationQueryService: EventRecommendationQueryService,
    private val eventQueryService: EventQueryService,
) {
    fun getEventFeeds(
        user: User?,
        category: String?,
    ): List<EventFeedQueryResponse> {
        val events =
            if (user == null) {
                guestFeed(category)
            } else {
                personalizedFeed(user.requireId(), category)
            }

        return events.map { event ->
            val likedEvent = eventQueryService.getLikedEvent(event.requireId(), user)
            EventFeedQueryResponse.from(likedEvent)
        }
    }

    private fun guestFeed(category: String?): List<Event> =
        topicListQueryService
            .findTopTopicsSinceLast24Hours(5, category)
            .map { it.getLastEvent() }

    private fun personalizedFeed(
        userId: Long,
        category: String?,
    ) = getSubscribedLastEvents(userId)
        .union(eventRecommendationQueryService.getRecommendedEvents(userId, category))
        .toList()

    private fun getSubscribedLastEvents(userId: Long): List<Event> =
        topicSubscriptionRepository
            .findByUserId(userId)
            .map { subscription -> subscription.topic.getLastEvent() }
            .filter { event ->
                interactionRepository.findByUserIdAndEventId(userId, event.requireId()).isEmpty()
            }
}
