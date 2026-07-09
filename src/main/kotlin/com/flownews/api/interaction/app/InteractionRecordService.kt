package com.flownews.api.interaction.app

import com.flownews.api.common.app.NoDataException
import com.flownews.api.event.app.EventQueryService
import com.flownews.api.interaction.domain.Interaction
import com.flownews.api.interaction.domain.InteractionRepository
import com.flownews.api.user.domain.User
import com.flownews.api.user.domain.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InteractionRecordService(
    private val interactionRepository: InteractionRepository,
    private val userRepository: UserRepository,
    private val eventQueryService: EventQueryService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun recordInteraction(
        request: InteractionRecordRequest,
        user: User,
    ): Interaction {
        val (eventId, interactionType) = request
        val userId = user.requireId()

        val user = findUserById(userId)
        val event = eventQueryService.findEventById(eventId)

        val interaction =
            Interaction(
                user = user,
                event = event,
                interactionType = interactionType,
            )

        eventPublisher.publishEvent(
            EventProfileUpdateEvent(
                userId = userId,
                eventIds = listOf(eventId),
                action = interactionType,
            ),
        )

        return interactionRepository.save(interaction)
    }

    private fun findUserById(userId: Long): User =
        userRepository.findById(userId).orElseThrow { NoDataException("User not found with id: $userId") }
}
