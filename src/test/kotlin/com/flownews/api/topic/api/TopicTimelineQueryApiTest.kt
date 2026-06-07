package com.flownews.api.topic.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.topic.app.ArticleResponse
import com.flownews.api.topic.app.EventItemQueryResponse
import com.flownews.api.topic.app.TopicTimelineQueryResponse
import com.flownews.api.topic.app.TopicTimelineQueryService
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
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ExtendWith(RestDocumentationExtension::class)
class TopicTimelineQueryApiTest {
    private lateinit var mockMvc: MockMvc

    private val topicTimelineQueryService = mockk<TopicTimelineQueryService>()

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = TopicTimelineQueryApi(topicTimelineQueryService)

        this.mockMvc = MockMvcTestUtils.createMockMvc(controller, restDocumentation)
    }

    @Test
    fun `토픽 타임라인을 조회한다`() {
        val mockResponse =
            TopicTimelineQueryResponse(
                id = 1L,
                title = "인공지능 발전",
                description = "AI 기술의 최신 동향과 발전사항",
                events =
                    listOf(
                        EventItemQueryResponse(
                            id = 1L,
                            title = "ChatGPT 4.0 출시",
                            description = "OpenAI가 새로운 ChatGPT 버전을 공개했습니다",
                            imageUrl = "https://example.com/image1.jpg",
                            eventTime = LocalDateTime.of(2024, 12, 4, 10, 0, 0),
                            articles =
                                listOf(
                                    ArticleResponse(
                                        id = 201L,
                                        title = "ChatGPT 4.0의 새로운 기능들",
                                        source = "OpenAI",
                                        url = "https://example.com/article2",
                                    ),
                                ),
                            likeCount = 15L,
                            isActive = true,
                        ),
                    ),
                isFollowing = true,
            )

        every { topicTimelineQueryService.getTopic(any(), any()) } returns mockResponse

        mockMvc.perform(
            get("/api/topics/{topicId}", 1L),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "topic-timeline-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("topicId").description("토픽 ID"),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("토픽 타임라인 데이터"),
                        fieldWithPath("data.id").description("토픽 ID"),
                        fieldWithPath("data.title").description("토픽 제목"),
                        fieldWithPath("data.description").description("토픽 설명"),
                        fieldWithPath("data.isFollowing").description("사용자의 토픽 팔로우 여부"),
                        fieldWithPath("data.events").description("타임라인 이벤트 목록"),
                        fieldWithPath("data.events[].id").description("이벤트 ID"),
                        fieldWithPath("data.events[].title").description("이벤트 제목"),
                        fieldWithPath("data.events[].description").description("이벤트 설명"),
                        fieldWithPath("data.events[].imageUrl").description("이벤트 이미지 URL"),
                        fieldWithPath("data.events[].eventTime").description("이벤트 발생 시간"),
                        fieldWithPath("data.events[].likeCount").description("이벤트 좋아요 수"),
                        fieldWithPath("data.events[].isActive").description("이벤트 활성/좋아요 여부"),
                        fieldWithPath("data.events[].articles").description("이벤트 관련 기사 목록"),
                        fieldWithPath("data.events[].articles[].id").description("기사 ID"),
                        fieldWithPath("data.events[].articles[].title").description("기사 제목"),
                        fieldWithPath("data.events[].articles[].source").description("기사 출처"),
                        fieldWithPath("data.events[].articles[].url").description("기사 URL"),
                    ),
                ),
            )
    }
}
