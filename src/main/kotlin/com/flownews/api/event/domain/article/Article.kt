package com.flownews.api.event.domain.article

import BaseEntity
import com.flownews.api.event.domain.Event
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "articles")
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    var event: Event,
    @Column(name = "title")
    var title: String,
    @Column(name = "category")
    var category: String? = null,
    @Column(name = "summary")
    var summary: String? = null,
    @Column(name = "source")
    var source: String,
    @Column(name = "url")
    var url: String,
) : BaseEntity() {
    fun requireId(): Long = id ?: throw IllegalStateException("Article ID cannot be null")
}
