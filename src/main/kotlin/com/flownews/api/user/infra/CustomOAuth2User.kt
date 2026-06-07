package com.flownews.api.user.infra

import com.flownews.api.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val attributes: Map<String, Any?>,
    private val user: User,
) : OAuth2User,
    UserDetails {
    fun getUser(): User = user

    override fun getAttributes(): Map<String, Any?> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(
            SimpleGrantedAuthority("ROLE_${user.role.name}"),
        )

    override fun getName(): String = user.id?.toString() ?: "anonymous"

    // UserDetails 구현
    override fun getUsername(): String = user.email

    override fun getPassword(): String? = null // 소셜 로그인은 패스워드 없음

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = !user.isDeleted()
}
