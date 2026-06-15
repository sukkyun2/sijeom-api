package com.flownews.api.event.domain

import BaseEntity
import com.flownews.api.event.domain.article.Article
import com.flownews.api.topic.domain.Topic
import com.flownews.api.topic.domain.TopicEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Formula
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "title")
    var title: String,
    @Column(name = "summary", columnDefinition = "text")
    var description: String,
    @Column(name = "image_url")
    var imageUrl: String,
    @Column(name = "event_time")
    var eventTime: LocalDateTime,
    @Column(name = "category")
    var category: String,
    @Column(name = "view_count")
    var viewCount: Long = 0,
    @Column(name = "embedding", columnDefinition = "public.vector(1536)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    var embedding: FloatArray? = null,
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    var articles: MutableList<Article> = mutableListOf(),
    @OneToMany(mappedBy = "event")
    var topicEvents: MutableList<TopicEvent> = mutableListOf(),
    @Formula("(SELECT COUNT(*) FROM likes l WHERE l.event_id = id AND l.deleted_at IS NULL)")
    val totalLikesCount: Long = 0,
) : BaseEntity() {
    fun requireId(): Long = id ?: throw IllegalStateException("Event ID cannot be null")

    fun getLikeCount(): Long = totalLikesCount

    fun getFirstTopic(): Topic = topicEvents.first().topic
}
