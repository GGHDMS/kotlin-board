package project.kotlin_board.security.jwt

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import project.kotlin_board.dto.UserDto
import project.kotlin_board.model.repository.UserRepository

@Component
class JwtProvider(
    private val userRepository: UserRepository,
    private val jwtExtractor: JwtExtractor
) {

    fun authenticate(token: String): Authentication {
        val email = jwtExtractor.extractEmail(token)
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Jwt 토큰에 해당하는 유저가 존재하지 않습니다.")

        return UsernamePasswordAuthenticationToken(
            UserDto(
                id = user.id,
                email = user.email,
                username = user.username,
                role = user.role
            ), "", listOf(SimpleGrantedAuthority(user.role.name))
        )
    }
}
