package com.flownews.config.security

import org.springframework.http.HttpMethod

enum class Access { PERMIT_ALL, REQUIRE_USER }

data class SecurityRule(val method: HttpMethod?, val pattern: String, val access: Access)

object SecurityRules {
    val rules: List<SecurityRule> =
        listOf(
            SecurityRule(HttpMethod.GET, "/clientsvc/topics/**", Access.PERMIT_ALL),
            SecurityRule(HttpMethod.GET, "/clientsvc/events/feed", Access.PERMIT_ALL),
            SecurityRule(null, "/intsvc/**", Access.PERMIT_ALL),
            SecurityRule(null, "/clientsvc/**", Access.REQUIRE_USER),
            SecurityRule(null, "/**", Access.REQUIRE_USER),
        )
}
