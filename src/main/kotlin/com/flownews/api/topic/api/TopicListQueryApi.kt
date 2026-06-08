package com.flownews.api.topic.api

import com.flownews.api.common.api.ApiResponse
import com.flownews.api.topic.app.TopicListQueryRequest
import com.flownews.api.topic.app.TopicListQueryResponse
import com.flownews.api.topic.app.TopicListQueryService
import com.flownews.api.topic.app.TopicTopKQueryResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TopicListQueryApi(
    private val topicListQueryService: TopicListQueryService,
) {
    @GetMapping("/clientsvc/topics")
    fun getAllTopics(
        @ModelAttribute req: TopicListQueryRequest,
    ): ApiResponse<List<TopicListQueryResponse>> = ApiResponse.ok(topicListQueryService.getTopics(req))

    @GetMapping("/clientsvc/topics/topk")
    fun getTopKTopics(
        @RequestParam(required = false) limit: Int = 5,
    ): ApiResponse<List<TopicTopKQueryResponse>> = ApiResponse.ok(topicListQueryService.getTopKTopics(limit))

    @GetMapping("/clientsvc/topics/search")
    fun searchTopics(
        @ModelAttribute req: TopicListQueryRequest,
    ): ApiResponse<out Any?> {
        return try {
            val topics = topicListQueryService.getTopicsByKeyword(req)
            ApiResponse.ok(topics)
        } catch (e: IllegalArgumentException) {
            ApiResponse.badRequest(e.message)
        }
    }
}
