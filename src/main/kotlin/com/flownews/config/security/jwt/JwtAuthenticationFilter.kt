package com.flownews.config.security.jwt

import com.flownews.api.user.domain.User
import com.flownews.api.user.domain.UserRepository
import com.flownews.api.user.infra.CustomOAuth2User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        authenticateRequest(request)
        filterChain.doFilter(request, response)
    }

    private fun authenticateRequest(request: HttpServletRequest) {
        resolveToken(request)
            ?.takeIf { jwtService.validateToken(it) }
            ?.let { jwtService.getId(it) }
            ?.let { userRepository.findByIdOrNull(it) }
            ?.takeIf { !it.isDeleted() }
            ?.let { setAuthentication(it, request) }
    }

    private fun setAuthentication(
        user: User,
        request: HttpServletRequest,
    ) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        val authentication =
            UsernamePasswordAuthenticationToken(
                CustomOAuth2User(emptyMap(), user),
                null,
                authorities,
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
