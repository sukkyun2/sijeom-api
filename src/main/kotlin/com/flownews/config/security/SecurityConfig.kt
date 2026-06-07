package com.flownews.config.security

import com.flownews.api.user.domain.UserRepository
import com.flownews.config.security.handler.JsonAccessDeniedHandler
import com.flownews.config.security.handler.JsonAuthenticationEntryPoint
import com.flownews.config.security.handler.OAuth2LoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

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
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health").permitAll()
                it.requestMatchers("/api/login/**").permitAll()
                it.requestMatchers("/api/token/refresh").permitAll()
                it.requestMatchers("/api/topics/**").permitAll()
                it.requestMatchers("/api/events/**").permitAll()
                it.requestMatchers("/notifications/push").permitAll()
                it.anyRequest().authenticated()
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
}
