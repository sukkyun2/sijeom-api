package com.flownews.config.security

import com.flownews.api.user.domain.UserRepository
import com.flownews.api.user.domain.enums.Role
import com.flownews.config.security.handler.JsonAccessDeniedHandler
import com.flownews.config.security.handler.JsonAuthenticationEntryPoint
import com.flownews.config.security.handler.OAuth2LoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

private typealias AuthRegistry = AuthorizeHttpRequestsConfigurer<*>.AuthorizationManagerRequestMatcherRegistry

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val accessDeniedHandler: JsonAccessDeniedHandler,
    private val authenticationEntryPoint: JsonAuthenticationEntryPoint,
    private val oauth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                SecurityRules.rules.forEach { it.applyTo(auth) }
            }.oauth2Login {
                it.successHandler(oauth2LoginSuccessHandler)
            }.exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
        http.addFilterBefore(
            JwtAuthenticationFilter(jwtService, userRepository),
            UsernamePasswordAuthenticationFilter::class.java,
        )

        return http.build()
    }

    private fun SecurityRule.applyTo(auth: AuthRegistry) {
        val matcher = if (method != null) auth.requestMatchers(method, pattern) else auth.requestMatchers(pattern)
        when (access) {
            Access.PERMIT_ALL -> matcher.permitAll()
            Access.REQUIRE_USER -> matcher.hasRole(Role.USER.name)
            Access.REQUIRE_ADMIN -> matcher.hasRole(Role.ADMIN.name)
        }
    }
}
