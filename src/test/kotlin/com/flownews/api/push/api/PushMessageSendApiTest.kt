package com.flownews.api.push.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.push.app.TopicEventPushService
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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.controller

@ExtendWith(RestDocumentationExtension::class)
class PushMessageSendApiTest {
    private lateinit var mockMvc: MockMvc
    private val pushSender = mockk<TopicEventPushService>(relaxed = true)

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = PushMessageSendApi(pushSender)
        this.mockMvc = MockMvcTestUtils.createMockMvcWithoutAuth(controller, restDocumentation)
    }

    @Test
    fun `토픽 기반으로 푸시 메시지를 전송한다`() {
        mockMvc.perform(
            post("/intsvc/notifications/push?by=topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"topicId": 1}"""),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "push-message-send-by-topic",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("by").description("Push 종류"),
                    ),
                    requestFields(
                        fieldWithPath("topicId").description("토픽 ID"),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithOptionalData(),
                    ),
                ),
            )
    }
}
