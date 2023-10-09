package project.kotlin_board.security.jwt

import io.jsonwebtoken.*
import org.springframework.stereotype.Component
import project.kotlin_board.security.exception.AuthenticateException

@Component
class JwtExtractor(
    private val jwtProperties: JwtProperties
) {

    fun extractEmail(token: String): String {
        return extractAllClaims(token).get("email", String::class.java)
            ?: throw AuthenticateException("JWT 토큰에 email 클레임이 없습니다.")
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .setSigningKey(jwtProperties.secretKey.toByteArray())
                .parseClaimsJws(token)
                .body
        } catch (expiredJwtException: ExpiredJwtException) {
            throw AuthenticateException("만료된 Jwt 토큰입니다.")
        } catch (unsupportedJwtException: UnsupportedJwtException) {
            throw AuthenticateException("지원되지 않는 Jwt 토큰입니다.")
        } catch (malformedJwtException: MalformedJwtException) {
            throw AuthenticateException("잘못된 형식의 Jwt 토큰입니다.")
        } catch (signatureException: SignatureException) {
            throw AuthenticateException("잘못된 Jwt Signature 값입니다.")
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw AuthenticateException("질못된 Jwt 헤더 값입니다.")
        }
    }
}
