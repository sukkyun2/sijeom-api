package com.flownews.api.interaction.app

import com.flownews.api.interaction.domain.InteractionType

data class EventProfileUpdateEvent(
    val userId: Long,
    val eventIds: List<Long>,
    val action: InteractionType,
)
