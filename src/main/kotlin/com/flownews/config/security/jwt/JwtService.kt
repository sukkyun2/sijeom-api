package com.flownews.config.security.jwt

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtService(
    private val properties: JwtProperties,
) {
    private val secretKey: SecretKey = generateSecretKey()
    private val parser: JwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()

    private fun generateSecretKey(): SecretKey {
        val secret =
            properties.secret
                ?: throw IllegalStateException("JWT_SECRET 환경변수가 설정되어 있지 않습니다.")
        val decodedKey = Base64.getDecoder().decode(secret)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, SignatureAlgorithm.HS256.jcaName)
    }

    fun createToken(id: String): String {
        val now = Date()
        val expiry = Date(now.time + properties.expiration)

        return Jwts
            .builder()
            .setSubject(id)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        try {
            parser.parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getId(token: String): Long? =
        try {
            val claims = parser.parseClaimsJws(token)
            claims.body.subject.toLongOrNull()
        } catch (e: Exception) {
            null
        }
}
