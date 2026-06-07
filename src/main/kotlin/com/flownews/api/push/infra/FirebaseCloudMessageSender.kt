package com.flownews.api.push.infra

import com.flownews.api.push.domain.PushLog
import com.flownews.api.push.domain.PushLogRepository
import com.flownews.api.push.domain.PushMessage
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
class FirebaseCloudMessageSender(
    private val firebaseMessaging: FirebaseMessaging,
    private val pushLogRepository: PushLogRepository,
) : MessageSender {
    override fun sendMessages(messages: List<PushMessage>) {
        if (messages.isEmpty()) return

        val firebaseMessages = messages.map { it.toFirebaseMessage() }
        firebaseMessaging.sendEach(firebaseMessages)

        pushLogRepository.saveAll(messages.map(::PushLog))
    }

    fun PushMessage.toFirebaseMessage(): Message {
        return Message.builder()
            .setToken(deviceToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(content)
                    .build(),
            )
            .putData("topicId", topicId.toString())
            .putData("eventId", eventId.toString())
            .build()
    }
}
