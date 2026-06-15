package com.flownews.api.event.domain.reaction

import org.springframework.data.jpa.repository.JpaRepository

interface EventLikeRepository : JpaRepository<EventLike, Long> {
    fun findByUserIdAndEventId(
        userId: Long,
        eventId: Long,
    ): EventLike?

    fun existsByUserIdAndEventId(
        userId: Long,
        eventId: Long,
    ): Boolean
}
