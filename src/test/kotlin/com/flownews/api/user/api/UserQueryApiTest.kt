package com.flownews.api.user.api

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.flownews.testutils.ApiResponseFieldSpecs
import com.flownews.testutils.MockMvcTestUtils
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(RestDocumentationExtension::class)
class UserQueryApiTest {
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        val controller = UserQueryApi()

        this.mockMvc = MockMvcTestUtils.createMockMvc(controller, restDocumentation)
    }

    @Test
    fun `현재 로그인한 사용자를 조회한다`() {
        mockMvc.perform(
            get("/clientsvc/users/me")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "user-current-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        *ApiResponseFieldSpecs.responseWithData("Current user data"),
                        fieldWithPath("data.id").description("사용자 ID"),
                        fieldWithPath("data.name").description("사용자 이름"),
                        fieldWithPath("data.email").description("사용자 이메일"),
                        fieldWithPath("data.profileUrl").description("사용자 프로필 이미지 URL").optional(),
                        fieldWithPath("data.role").description("사용자 역할 (USER, ADMIN)"),
                    ),
                ),
            )
    }
}
