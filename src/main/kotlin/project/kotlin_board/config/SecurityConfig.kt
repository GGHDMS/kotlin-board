package project.kotlin_board.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.DefaultSecurityFilterChain
import project.kotlin_board.model.Role
import project.kotlin_board.security.handler.CustomAccessDeniedHandler
import project.kotlin_board.security.jwt.JwtProvider

@Configuration
class SecurityConfig(
    private val jwtProvider: JwtProvider,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {

    @Bean
    fun filterChain(http: HttpSecurity): DefaultSecurityFilterChain = http
        .httpBasic { it.disable() }
        .csrf { it.disable() }
        .logout { it.disable() }
        .formLogin { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests {
            it
                .antMatchers("/api/users/sign-up", "/api/users/sign-in", "/v3/**", "/swagger-ui/**").permitAll()
                .antMatchers("/api/show").hasAuthority(Role.ADMIN.name)
                .anyRequest().authenticated()
                .and()
                .apply(JwtSecurityConfig(jwtProvider, authenticationEntryPoint))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
        }
        .build()

    @Bean
    fun encodePassword(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
