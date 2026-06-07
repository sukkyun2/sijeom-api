package com.flownews.api.user.app

import com.flownews.api.user.domain.enums.ClientPlatform

interface AuthRedirectStrategy {
    fun buildSuccessUrl(authResult: AuthResult): String

    fun buildErrorUrl(platform: ClientPlatform, error: String): String
}