package com.flownews.api.user.app

import com.flownews.api.user.domain.User
import com.flownews.api.user.domain.enums.ClientPlatform
import com.flownews.config.security.jwt.JwtService
import org.springframework.stereotype.Service

@Service
class OAuth2AuthTokenService(
    private val jwtService: JwtService,
) {
    fun issue(
        user: User,
        platform: ClientPlatform,
    ): AuthResult {
        val accessToken = jwtService.createToken(user.requireId().toString())
        return AuthResult(accessToken = accessToken, platform = platform)
    }
}
