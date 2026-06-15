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
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,
    @Column(name = "is_deleted")
    var isDeleted: LocalDateTime? = null,
) : BaseEntity() {
    fun delete() {
        this.isDeleted = LocalDateTime.now()
    }

    companion object {
        fun of(
            user: User,
            event: Event,
        ): Like {
            return Like(
                user = user,
                event = event,
                isDeleted = null,
            )
        }
    }
}
