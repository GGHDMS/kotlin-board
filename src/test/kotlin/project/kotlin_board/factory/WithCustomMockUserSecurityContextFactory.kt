package project.kotlin_board.factory

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import project.kotlin_board.annotation.WithCustomMockUser
import project.kotlin_board.dto.UserDto


class WithCustomMockUserSecurityContextFactory : WithSecurityContextFactory<WithCustomMockUser> {
    override fun createSecurityContext(mockUser: WithCustomMockUser): SecurityContext {
        val auth: Authentication = UsernamePasswordAuthenticationToken(
            UserDto(
                id = mockUser.userId,
                email = mockUser.email,
                username = mockUser.username,
                role = mockUser.role
            ), "", listOf(SimpleGrantedAuthority(mockUser.role.name))
        )
        val context = SecurityContextHolder.getContext()
        context.authentication = auth
        return context
    }
}
