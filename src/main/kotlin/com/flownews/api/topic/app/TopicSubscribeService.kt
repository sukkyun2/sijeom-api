package com.flownews.api.topic.app

import com.flownews.api.common.app.NoDataException
import com.flownews.api.interaction.domain.InteractionType
import com.flownews.api.interaction.infra.TopicBasedProfileUpdateRequest
import com.flownews.api.interaction.infra.UserProfileApiClient
import com.flownews.api.topic.domain.Topic
import com.flownews.api.topic.domain.TopicRepository
import com.flownews.api.topic.domain.TopicSubscription
import com.flownews.api.topic.domain.TopicSubscriptionRepository
import com.flownews.api.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicSubscribeService(
    private val topicRepository: TopicRepository,
    private val topicSubscriptionRepository: TopicSubscriptionRepository,
    private val userProfileApiClient: UserProfileApiClient,
) {
    @Transactional
    fun toggleSubscription(req: TopicSubscribeRequest) {
        val topic = getTopic(req.topicId)
        val user = req.user

        val userId = user.requireId()
        val topicId = topic.requireId()
        val subscription = topicSubscriptionRepository.findByTopicIdAndUserId(topicId, userId)

        subscription?.let { unsubscribe(it) } ?: subscribe(user, topic)
    }

    private fun subscribe(
        user: User,
        topic: Topic,
    ) {
        saveSubscription(user, topic)
        userProfileApiClient.updateProfileByTopic(
            userId = user.requireId(),
            request =
                TopicBasedProfileUpdateRequest(
                    userId = user.requireId(),
                    topicIds = listOf(topic.requireId()),
                    action = InteractionType.TOPIC_FOLLOWED,
                ),
        )
    }

    private fun unsubscribe(subscription: TopicSubscription) {
        topicSubscriptionRepository.delete(subscription)
        userProfileApiClient.updateProfileByTopic(
            userId = subscription.user.requireId(),
            request =
                TopicBasedProfileUpdateRequest(
                    userId = subscription.user.requireId(),
                    topicIds = listOf(subscription.topic.requireId()),
                    action = InteractionType.TOPIC_UNFOLLOWED,
                ),
        )
    }

    private fun saveSubscription(
        user: User,
        topic: Topic,
    ) {
        topicSubscriptionRepository.save(TopicSubscription(user = user, topic = topic))
    }

    private fun getTopic(topicId: Long): Topic =
        topicRepository
            .findById(topicId)
            .orElseThrow { NoDataException("Topic not found : $topicId") }
}
