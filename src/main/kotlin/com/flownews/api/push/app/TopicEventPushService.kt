package com.flownews.api.push.app

import com.flownews.api.push.domain.PushMessage
import com.flownews.api.push.infra.MessageSender
import com.flownews.api.topic.domain.TopicQueryService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class TopicEventPushService(
    private val topicQueryService: TopicQueryService,
    private val messageSender: MessageSender,
) {
    @PreAuthorize("hasRole('ADMIN')")
    fun sendPushMessages(topicId: Long) {
        val topicWithSubscribers = topicQueryService.getTopicWithSubscribers(topicId)
        val topic = topicWithSubscribers.topic
        val subscribers = topicWithSubscribers.getActiveSubscribers()
        val messages = subscribers.map { PushMessage(topic, it) }

        messageSender.sendMessages(messages)
    }
}
