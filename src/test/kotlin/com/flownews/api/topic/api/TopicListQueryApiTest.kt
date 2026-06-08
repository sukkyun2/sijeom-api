package com.flownews.api.topic.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.api.topic.app.TopicListQueryResponse
import com.flownews.api.topic.app.TopicListQueryService
import com.flownews.api.topic.app.TopicTopKQueryResponse
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

@ExtendWith(RestDocumentationExtension::class)
class TopicListQueryApiTest {
    private lateinit var mockMvc: MockMvc

    private val topicListQueryService = mockk<TopicListQueryService>()

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = TopicListQueryApi(topicListQueryService)

        this.mockMvc = MockMvcTestUtils.createMockMvcWithoutAuth(controller, restDocumentation)
    }

    @Test
    fun `전체 토픽 목록을 조회한다`() {
        val mockTopics =
            listOf(
                TopicListQueryResponse(
                    id = 1L,
                    title = "인공지능 발전",
                    description = "AI 기술의 최신 동향과 발전사항",
                ),
                TopicListQueryResponse(
                    id = 2L,
                    title = "블록체인 기술",
                    description = "블록체인과 암호화폐의 최신 소식",
                ),
            )

        every { topicListQueryService.getTopics(any()) } returns mockTopics

        mockMvc.perform(
            get("/clientsvc/topics")
                .param("page", "0")
                .param("size", "10"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "topic-list-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작, 선택사항)").optional(),
                        parameterWithName("size").description("페이지 크기 (선택사항)").optional(),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("토픽 목록 배열 데이터"),
                        fieldWithPath("data[].id").description("토픽 ID"),
                        fieldWithPath("data[].title").description("토픽 제목"),
                        fieldWithPath("data[].description").description("토픽 설명"),
                    ),
                ),
            )
    }

    @Test
    fun `상위 K개 토픽을 조회한다`() {
        val mockTopKTopics =
            listOf(
                TopicTopKQueryResponse(
                    id = 1L,
                    title = "인공지능 발전",
                ),
            )

        every { topicListQueryService.getTopKTopics(any()) } returns mockTopKTopics

        mockMvc.perform(
            get("/clientsvc/topics/topk")
                .param("limit", "5"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "topic-topk-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("limit").description("조회할 상위 토픽 수 (기본값: 5)").optional(),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("상위 K개 토픽 배열 데이터"),
                        fieldWithPath("data[].id").description("토픽 ID"),
                        fieldWithPath("data[].title").description("토픽 제목"),
                    ),
                ),
            )
    }

    @Test
    fun `키워드로 토픽을 검색한다`() {
        val mockSearchResults =
            listOf(
                TopicListQueryResponse(
                    id = 1L,
                    title = "인공지능 발전",
                    description = "AI 기술의 최신 동향과 발전사항",
                ),
            )

        every { topicListQueryService.getTopicsByKeyword(any()) } returns mockSearchResults

        mockMvc.perform(
            get("/clientsvc/topics/search")
                .param("keyword", "AI"),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "topic-search-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("keyword").description("토픽 제목 또는 설명 검색 키워드"),
                    ),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("검색 결과 배열 데이터"),
                        fieldWithPath("data[].id").description("토픽 ID"),
                        fieldWithPath("data[].title").description("토픽 제목"),
                        fieldWithPath("data[].description").description("토픽 설명"),
                    ),
                ),
            )
    }
}
