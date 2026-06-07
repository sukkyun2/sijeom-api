package com.flownews.api.event.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.event.app.EventFeedQueryResponse
import com.flownews.api.event.app.EventFeedQueryService
import com.flownews.api.event.app.TopicSimpleInfo
import com.flownews.testutils.ApiResponseFieldSpecs
import com.flownews.testutils.MockMvcTestUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ExtendWith(RestDocumentationExtension::class)
class EventFeedQueryApiTest {
    private lateinit var mockMvc: MockMvc

    private val eventFeedQueryService = mockk<EventFeedQueryService>()

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = EventFeedQueryApi(eventFeedQueryService)
        this.mockMvc = MockMvcTestUtils.createMockMvc(controller, restDocumentation)
    }

    @Test
    fun `이벤트 피드를 조회한다`() {
        val mockResponse =
            listOf(
                EventFeedQueryResponse(
                    id = 1L,
                    topic = TopicSimpleInfo(1L, "AI 기술"),
                    title = "AI 기술의 새로운 혁신",
                    description = "최신 AI 기술 동향과 발전 방향",
                    imageUrl = "https://example.com/image1.jpg",
                    eventTime = LocalDateTime.now(),
                    likeCount = 15L,
                ),
            )

        every { eventFeedQueryService.getEventFeeds(any(), any()) } returns mockResponse

        mockMvc.perform(
            get("/api/events/feed")
                .param("category", "기술"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "event-feed-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("category")
                            .description("피드 카테고리 필터").optional(),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("피드 리스트"),
                        fieldWithPath("data[].id").description("피드 ID"),
                        fieldWithPath("data[].topic").description("토픽 정보"),
                        fieldWithPath("data[].topic.id").description("토픽 ID"),
                        fieldWithPath("data[].topic.title").description("토픽 제목"),
                        fieldWithPath("data[].title").description("피드 제목"),
                        fieldWithPath("data[].description").description("피드 설명"),
                        fieldWithPath("data[].imageUrl").description("피드 이미지 URL"),
                        fieldWithPath("data[].eventTime").description("피드 발생 시간(YYYY-MM-DDTHH:mm:ss)"),
                        fieldWithPath("data[].likeCount").description("피드 좋아요 수"),
                    ),
                ),
            )
    }
}
