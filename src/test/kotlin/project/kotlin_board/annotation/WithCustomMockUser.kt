package project.kotlin_board.annotation

import org.springframework.security.test.context.support.WithSecurityContext
import project.kotlin_board.factory.WithCustomMockUserSecurityContextFactory
import project.kotlin_board.model.Role

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory::class)
annotation class WithCustomMockUser(
    val userId: Long = 1L,
    val email: String = "email@email.com",
    val username: String = "username",
    val role: Role = Role.USER
)
