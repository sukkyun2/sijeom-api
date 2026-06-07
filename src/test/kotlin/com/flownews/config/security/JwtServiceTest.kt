package com.flownews.config.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Base64
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class JwtServiceTest {
    private val testSecret =
        Base64.getEncoder().encodeToString(
            "test-secret-key-that-is-32bytes!".toByteArray(),
        )
    private val sut = JwtService(JwtProperties(secret = testSecret, expiration = 1.hours.inWholeMilliseconds))

    @Test
    fun `createToken은 주어진 ID를 subject로 담은 토큰을 생성한다`() {
        val token = sut.createToken("42")

        assertNotNull(token)
        assertEquals(42L, sut.getId(token))
    }

    @Test
    fun `validateToken은 유효한 토큰에 대해 true를 반환한다`() {
        val token = sut.createToken("1")

        assertTrue(sut.validateToken(token))
    }

    @Test
    fun `validateToken은 만료된 토큰에 대해 false를 반환한다`() {
        val expiredService =
            JwtService(JwtProperties(secret = testSecret, expiration = (-1).seconds.inWholeMilliseconds))
        val expiredToken = expiredService.createToken("1")

        assertFalse(sut.validateToken(expiredToken))
    }

    @Test
    fun `validateToken은 변조된 토큰에 대해 false를 반환한다`() {
        val token = sut.createToken("1")
        val tampered = token.dropLast(5) + "XXXXX"

        assertFalse(sut.validateToken(tampered))
    }

    @Test
    fun `validateToken은 빈 문자열에 대해 false를 반환한다`() {
        assertFalse(sut.validateToken(""))
    }

    @Test
    fun `getId는 유효한 토큰에서 ID를 반환한다`() {
        val token = sut.createToken("99")

        assertEquals(99L, sut.getId(token))
    }

    @Test
    fun `getId는 subject가 숫자가 아닌 토큰에 대해 null을 반환한다`() {
        val token = sut.createToken("not-a-number")

        assertNull(sut.getId(token))
    }

    @Test
    fun `getId는 유효하지 않은 토큰에 대해 null을 반환한다`() {
        assertNull(sut.getId("invalid.token.string"))
    }
}
