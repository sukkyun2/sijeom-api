package com.flownews.api.user.app

import com.flownews.api.user.domain.enums.ClientPlatform

data class AuthResult(
    val accessToken: String,
    val platform: ClientPlatform,
)