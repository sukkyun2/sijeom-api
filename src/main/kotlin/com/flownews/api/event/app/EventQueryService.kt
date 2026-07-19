package com.flownews.api.event.app

import com.flownews.api.common.app.NoDataException
import com.flownews.api.event.domain.Event
import com.flownews.api.event.domain.EventRepository
import com.flownews.api.event.domain.LikedEvent
import com.flownews.api.event.domain.reaction.EventLikeRepository
import com.flownews.api.user.domain.User
import org.springframework.stereotype.Service

@Service
class EventQueryService(
    private val eventRepository: EventRepository,
    private val eventLikeRepository: EventLikeRepository,
) {
    fun getLikedEvent(
        id: Long,
        user: User?,
    ): LikedEvent {
        val event = findEventById(id)
        if (user == null) {
            return LikedEvent(event, false)
        }

        val isLiked = eventLikeRepository.existsByUserIdAndEventId(user.requireId(), id)

        return LikedEvent(event, isLiked)
    }

    fun findEventById(id: Long): Event {
        return eventRepository.findById(id).orElseThrow {
            NoDataException("Event not found: $id")
        }
    }
}
