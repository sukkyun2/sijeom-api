package com.flownews.api.user.domain.enums

enum class ClientPlatform {
    WEB,
    MOBILE,
    ;

    companion object {
        fun from(registrationId: String): ClientPlatform =
            if (registrationId.endsWith("-mobile")) MOBILE else WEB
    }
}