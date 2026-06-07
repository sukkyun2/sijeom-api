package com.flownews.config.security

import org.springframework.http.HttpMethod

enum class Access { PERMIT_ALL, REQUIRE_USER, REQUIRE_ADMIN }

data class SecurityRule(val method: HttpMethod?, val pattern: String, val access: Access)

object SecurityRules {
    val rules: List<SecurityRule> =
        listOf(
            SecurityRule(HttpMethod.GET, "/api/topics/**", Access.PERMIT_ALL),
            SecurityRule(HttpMethod.GET, "/api/events/feed", Access.PERMIT_ALL),
            SecurityRule(null, "/api/notifications/**", Access.REQUIRE_ADMIN),
            SecurityRule(null, "/**", Access.REQUIRE_USER),
        )
}
