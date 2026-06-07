package com.flownews.config.security.handler

import com.flownews.api.user.app.AuthRedirectStrategy
import com.flownews.api.user.app.OAuth2AuthTokenService
import com.flownews.api.user.domain.enums.ClientPlatform
import com.flownews.api.user.infra.CustomOAuth2User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val authTokenService: OAuth2AuthTokenService,
    private val redirectStrategy: AuthRedirectStrategy,
) : SavedRequestAwareAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val customUser = authentication.principal as CustomOAuth2User
        val user = customUser.getUser()
        val platform =
            ClientPlatform.from(
                (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId,
            )

        if (user.isDeleted()) {
            response.sendRedirect(redirectStrategy.buildErrorUrl(platform, "DELETED"))
            return
        }

        val authResult = authTokenService.issue(user, platform)
        response.sendRedirect(redirectStrategy.buildSuccessUrl(authResult))
    }
}
