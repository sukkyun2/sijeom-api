package com.flownews.config.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod
import org.springframework.util.AntPathMatcher
import java.util.stream.Stream

class SecurityRulesTest {
    private val matcher = AntPathMatcher()

    // SecurityConfig.applyTo의 매칭 조건을 테스트하기 위한 헬퍼 메서드
    private fun resolveAccess(
        method: HttpMethod,
        path: String,
    ): Access =
        SecurityRules.rules
            .first { rule ->
                (rule.method == null || rule.method == method) && matcher.match(rule.pattern, path)
            }.access

    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("cases")
    fun `인가 규칙이 올바른 접근 수준을 반환한다`(
        method: HttpMethod,
        path: String,
        expected: Access,
    ) {
        assertEquals(expected, resolveAccess(method, path))
    }

    companion object {
        @JvmStatic
        fun cases(): Stream<Array<Any>> =
            Stream.of(
                arrayOf(HttpMethod.GET, "/api/topics", Access.PERMIT_ALL),
                arrayOf(HttpMethod.GET, "/api/topics/42", Access.PERMIT_ALL),
                arrayOf(HttpMethod.GET, "/api/events/feed", Access.PERMIT_ALL),
                arrayOf(HttpMethod.POST, "/api/topics/42", Access.REQUIRE_USER),
                arrayOf(HttpMethod.POST, "/api/events/feed", Access.REQUIRE_USER),
                arrayOf(HttpMethod.GET, "/api/notifications/push", Access.REQUIRE_ADMIN),
                arrayOf(HttpMethod.POST, "/api/notifications/push", Access.REQUIRE_ADMIN),
                arrayOf(HttpMethod.GET, "/api/users/me", Access.REQUIRE_USER),
                arrayOf(HttpMethod.POST, "/api/topics/1/subscribe", Access.REQUIRE_USER),
            )
    }
}
