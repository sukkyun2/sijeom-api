package com.flownews.api.interaction.infra

import com.flownews.api.interaction.app.EventProfileUpdateEvent
import com.flownews.api.interaction.app.TopicProfileUpdateEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserProfileUpdateEventListener(
    private val userProfileApiClient: UserProfileApiClient,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleTopicProfileUpdate(event: TopicProfileUpdateEvent) {
        userProfileApiClient.updateProfileByTopic(
            userId = event.userId,
            request =
                TopicBasedProfileUpdateRequest(
                    userId = event.userId,
                    topicIds = event.topicIds,
                    action = event.action,
                ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleEventProfileUpdate(event: EventProfileUpdateEvent) {
        userProfileApiClient.updateProfileByEvent(
            userId = event.userId,
            request =
                EventBasedProfileUpdateRequest(
                    userId = event.userId,
                    eventIds = event.eventIds,
                    action = event.action,
                ),
        )
    }
}
