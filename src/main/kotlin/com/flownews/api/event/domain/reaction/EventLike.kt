package com.flownews.api.event.domain.reaction

import BaseEntity
import com.flownews.api.event.domain.Event
import com.flownews.api.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
@SQLRestriction("deleted_at IS NULL")
class EventLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {
    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    companion object {
        fun of(
            user: User,
            event: Event,
        ): EventLike = EventLike(user = user, event = event)
    }
}
