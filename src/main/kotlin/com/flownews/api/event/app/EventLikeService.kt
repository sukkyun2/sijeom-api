package com.flownews.api.event.app

import com.flownews.api.event.domain.Event
import com.flownews.api.event.domain.reaction.EventLike
import com.flownews.api.event.domain.reaction.EventLikeRepository
import com.flownews.api.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EventLikeService(
    private val eventLikeRepository: EventLikeRepository,
    private val eventQueryService: EventQueryService,
) {
    fun toggleLike(
        eventId: Long,
        user: User,
    ) {
        val event = eventQueryService.findEventById(eventId)
        val existingLike = findExistingLike(user.requireId(), eventId)

        return if (existingLike == null) {
            addLike(event, user)
        } else {
            removeLike(existingLike)
        }
    }

    private fun findExistingLike(
        userId: Long,
        eventId: Long,
    ): EventLike? {
        return eventLikeRepository.findByUserIdAndEventId(userId, eventId)
    }

    private fun addLike(
        event: Event,
        user: User,
    ) {
        eventLikeRepository.save(EventLike.of(user, event))
    }

    private fun removeLike(eventLike: EventLike) {
        eventLike.delete()
        eventLikeRepository.save(eventLike)
    }
}
