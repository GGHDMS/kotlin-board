package project.kotlin_board.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): DefaultSecurityFilterChain = http
        .csrf().disable()
        .authorizeHttpRequests {
            it.anyRequest().permitAll()
        }
        .build()

    @Bean
    fun encodePassword(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
