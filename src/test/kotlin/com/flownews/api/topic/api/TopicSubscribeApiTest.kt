package com.flownews.api.topic.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.topic.app.TopicSubscribeService
import com.flownews.testutils.ApiResponseFieldSpecs
import com.flownews.testutils.MockMvcTestUtils
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(RestDocumentationExtension::class)
class TopicSubscribeApiTest {
    private lateinit var mockMvc: MockMvc

    private val topicSubscribeService = mockk<TopicSubscribeService>(relaxed = true)

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = TopicSubscribeApi(topicSubscribeService)

        this.mockMvc = MockMvcTestUtils.createMockMvc(controller, restDocumentation)
    }

    @Test
    fun `토픽 구독을 토글한다`() {
        mockMvc.perform(
            post("/api/topics/{topicId}/toggle-subscription", 1L)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "topic-subscription-toggle",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("topicId").description("토픽 ID"),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithOptionalData(),
                    ),
                ),
            )
    }
}
