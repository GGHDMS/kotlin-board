package project.kotlin_board.security.filter

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.filter.OncePerRequestFilter
import project.kotlin_board.security.exception.AuthenticateException
import project.kotlin_board.security.jwt.JwtProvider
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val AUTHORIZATION_HEADER = "Authorization"
private const val AUTHORIZATION_SCHEMA = "Bearer"

class JwtFilter(
    private val jwtProvider: JwtProvider,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = extractAccessTokenFromHeader(authorizationHeader)
            val authentication = jwtProvider.authenticate(token)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (exception : AuthenticateException) {
            authenticationEntryPoint.commence(request, response, exception)
        }

        filterChain.doFilter(request, response)
    }

    private fun extractAccessTokenFromHeader(authorizationHeader: String): String {
        val splits = authorizationHeader.split(" ")
        validateAccessToken(splits)
        return splits[1]
    }

    private fun validateAccessToken(splits: List<String>) {
        if (splits.size != 2) throw AuthenticateException("잘못된 형식의 Authorization 헤더값 입니다.")
        if (splits[0] != AUTHORIZATION_SCHEMA) throw AuthenticateException("잘못된 Authorization 스키마 입니다.")
    }
}
