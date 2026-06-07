package com.flownews.api.interaction.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.interaction.app.InteractionRecordService
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(RestDocumentationExtension::class)
class InteractionRecordApiTest {
    private lateinit var mockMvc: MockMvc
    private val interactionRecordService = mockk<InteractionRecordService>(relaxed = true)

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        var controller = InteractionRecordApi(interactionRecordService)
        this.mockMvc = MockMvcTestUtils.createMockMvc(controller, restDocumentation)
    }

    @Test
    fun `사용자 인터랙션을 기록한다`() {
        mockMvc.perform(
            post("/api/interactions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .content(
                    """
                    {
                        "eventId": 1,
                        "interactionType": "VIEWED"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "interaction-record",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("eventId").description("사용자가 상호작용한 이벤트 ID"),
                        fieldWithPath(
                            "interactionType",
                        ).description(
                            "상호작용 유형 (VIEWED, ARTICLE_CLICKED, TOPIC_VIEWED, TOPIC_FOLLOWED, TOPIC_UNFOLLOWED)",
                        ),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithOptionalData(),
                    ),
                ),
            )
    }
}
