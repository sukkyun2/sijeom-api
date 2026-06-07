package com.flownews.api.user.infra

import com.flownews.api.user.app.AuthResult
import com.flownews.api.user.domain.enums.ClientPlatform
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultAuthRedirectStrategyTest {
    private val sut =
        DefaultAuthRedirectStrategy(
            webBaseUri = "https://sijeom.kr",
            mobileScheme = "sijeom",
        )

    @Test
    fun `WEB 플랫폼 성공 URL은 webBaseUri 기반으로 생성된다`() {
        val authResult = AuthResult(accessToken = "access-token", platform = ClientPlatform.WEB)

        val url = sut.buildSuccessUrl(authResult)

        assertEquals("https://sijeom.kr/auth/callback?token=access-token", url)
    }

    @Test
    fun `MOBILE 플랫폼 성공 URL은 mobileScheme 기반으로 생성된다`() {
        val authResult = AuthResult(accessToken = "access-token", platform = ClientPlatform.MOBILE)

        val url = sut.buildSuccessUrl(authResult)

        assertEquals("sijeom://auth/callback?token=access-token", url)
    }

    @Test
    fun `WEB 플랫폼 에러 URL은 webBaseUri 기반으로 생성된다`() {
        val url = sut.buildErrorUrl(ClientPlatform.WEB, "DELETED")

        assertEquals("https://sijeom.kr/auth/callback?error=DELETED", url)
    }

    @Test
    fun `MOBILE 플랫폼 에러 URL은 mobileScheme 기반으로 생성된다`() {
        val url = sut.buildErrorUrl(ClientPlatform.MOBILE, "DELETED")

        assertEquals("sijeom://auth/callback?error=DELETED", url)
    }
}
