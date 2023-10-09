package project.kotlin_board.config

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import project.kotlin_board.security.filter.JwtFilter
import project.kotlin_board.security.jwt.JwtProvider

class JwtSecurityConfig(
    private val jwtProvider: JwtProvider,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(JwtFilter(jwtProvider, authenticationEntryPoint), UsernamePasswordAuthenticationFilter::class.java)
    }
}
