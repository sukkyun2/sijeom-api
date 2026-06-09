package com.flownews.config.security.jwt

import com.flownews.api.user.domain.User
import com.flownews.api.user.domain.UserRepository
import com.flownews.api.user.domain.enums.Role
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime

class JwtAuthenticationFilterTest {
    private val jwtService = mockk<JwtService>()
    private val userRepository = mockk<UserRepository>()
    private val filterChain = mockk<FilterChain>(relaxed = true)
    private val request = mockk<HttpServletRequest>(relaxed = true)
    private val response = mockk<HttpServletResponse>(relaxed = true)

    private val sut = JwtAuthenticationFilter(jwtService, userRepository)

    private fun mockValidToken(userId: Long = 1L) {
        every { request.getHeader("Authorization") } returns "Bearer valid.jwt.token"
        every { jwtService.validateToken("valid.jwt.token") } returns true
        every { jwtService.getId("valid.jwt.token") } returns userId
    }

    private fun activeUser() =
        User(
            id = 1L,
            oauthId = "oauth-id",
            provider = "google",
            name = "테스트유저",
            email = "test@example.com",
            role = Role.USER,
        )

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
        every { request.getAttribute(any()) } returns null
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `Authorization 헤더가 없으면 인증을 설정하지 않는다`() {
        every { request.getHeader("Authorization") } returns null

        sut.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `Bearer 프리픽스 없는 헤더는 인증을 설정하지 않는다`() {
        every { request.getHeader("Authorization") } returns "invalid-token"

        sut.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `유효하지 않은 토큰이면 인증을 설정하지 않는다`() {
        every { request.getHeader("Authorization") } returns "Bearer invalid.jwt.token"
        every { jwtService.validateToken("invalid.jwt.token") } returns false

        sut.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `토큰에서 추출한 ID로 유저를 찾을 수 없으면 인증을 설정하지 않는다`() {
        mockValidToken(userId = 99L)
        every { userRepository.findByIdOrNull(99L) } returns null

        sut.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `삭제된 유저이면 인증을 설정하지 않는다`() {
        val deletedUser = activeUser().apply { deletedAt = LocalDateTime.now() }
        mockValidToken()
        every { userRepository.findByIdOrNull(1L) } returns deletedUser

        sut.doFilter(request, response, filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `유효한 토큰과 활성 유저이면 SecurityContext에 인증을 설정한다`() {
        val user = activeUser()
        mockValidToken()
        every { userRepository.findByIdOrNull(1L) } returns user

        sut.doFilter(request, response, filterChain)

        assertNotNull(SecurityContextHolder.getContext().authentication)
    }
}
