package com.flownews.api.interaction.app

import com.flownews.api.interaction.domain.InteractionType

data class TopicProfileUpdateEvent(
    val userId: Long,
    val topicIds: List<Long>,
    val action: InteractionType,
)
