package project.kotlin_board.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import project.kotlin_board.model.Role
import java.util.*

@Component
class JwtGenerator(
    private val jwtProperties: JwtProperties,
) {

    fun generateAccessToken(email: String, role: Role): String {
        val claims = mutableMapOf<String, Any>().apply {
            put("email", email)
            put("role", role.name)
        }
        return createToken(claims, jwtProperties.accessExpTime)
    }

    fun generateRefreshToken(email: String, role: Role): String {
        val claims = mutableMapOf<String, Any>().apply {
            put("email", email)
            put("role", role.name)
        }
        return createToken(claims, jwtProperties.refreshExpTime)
    }

    private fun createToken(claims: MutableMap<String, Any>, expTime: Int): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expTime))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secretKey.toByteArray())
            .compact()
    }
}
