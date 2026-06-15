package com.flownews.api.event.domain.reaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LikeRepository : JpaRepository<Like, Long> {
    fun findByUserIdAndEventIdAndIsDeletedIsNull(
        userId: Long,
        eventId: Long,
    ): Like?

    @Query(
        """
        SELECT COUNT(l) 
        FROM Like l 
        WHERE l.event.id = :eventId 
        AND l.isDeleted IS NULL
        """,
    )
    fun countByEventIdAndIsDeletedIsNull(eventId: Long): Long

    fun existsByUserIdAndEventId(
        userId: Long,
        eventId: Long,
    ): Boolean
}
