package com.flownews.api.user.infra

import com.flownews.api.user.app.AuthRedirectStrategy
import com.flownews.api.user.app.AuthResult
import com.flownews.api.user.domain.enums.ClientPlatform
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DefaultAuthRedirectStrategy(
    @Value("\${spring.security.oauth2.client.base-uri}") private val webBaseUri: String,
    @Value("\${app.mobile.scheme}") private val mobileScheme: String,
) : AuthRedirectStrategy {
    override fun buildSuccessUrl(authResult: AuthResult): String =
        when (authResult.platform) {
            ClientPlatform.MOBILE ->
                "$mobileScheme://auth/callback?token=${authResult.accessToken}"
            ClientPlatform.WEB ->
                "$webBaseUri/auth/callback?token=${authResult.accessToken}"
        }

    override fun buildErrorUrl(
        platform: ClientPlatform,
        error: String,
    ): String =
        when (platform) {
            ClientPlatform.MOBILE -> "$mobileScheme://auth/callback?error=$error"
            ClientPlatform.WEB -> "$webBaseUri/auth/callback?error=$error"
        }
}
