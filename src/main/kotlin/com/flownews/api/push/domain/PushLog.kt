package com.flownews.api.push.domain

import BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "push_logs")
class PushLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id")
    val userId: Long,
    @Column(name = "device_token")
    val token: String,
    @Column(name = "message_title")
    val messageTitle: String,
    @Column(name = "message_body")
    val messageBody: String,
) : BaseEntity() {
    constructor(message: PushMessage) : this(
        userId = message.userId,
        token = message.deviceToken,
        messageTitle = message.title,
        messageBody = message.content,
    )
}
