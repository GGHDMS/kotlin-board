package project.kotlin_board.model.repository.querydsl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import project.kotlin_board.dto.response.UserWithTimeResponse
import project.kotlin_board.model.Role
import project.kotlin_board.model.entity.QUser.user
import java.time.LocalDate
import java.time.LocalTime

class UserRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory) : UserRepositoryCustom {
    override fun findUserListByQueryDsl(
        username: String?,
        email: String?,
        createdAtStart: LocalDate?,
        createdAtEnd: LocalDate?,
        updatedAtStart: LocalDate?,
        updatedAtEnd: LocalDate?,
    ): List<UserWithTimeResponse> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    UserWithTimeResponse::class.java,
                    user.id,
                    user.email,
                    user.username,
                    user.role,
                    user.createdAt,
                    user.updatedAt,
                ),
            )
            .from(user)
            .where(
                usernameEq(username),
                emailEq(email),
                timeBetween(createdAtStart, createdAtEnd),
                timeBetween(updatedAtStart, updatedAtEnd),
                user.role.eq(Role.USER),
            )
            .orderBy(user.id.desc())
            .fetch()
    }

    private fun usernameEq(username: String?): BooleanExpression? {
        return if (username.isNullOrBlank()) null else user.username.eq(username)
    }

    private fun emailEq(email: String?): BooleanExpression? {
        return if (email.isNullOrBlank()) null else user.email.eq(email)
    }

    private fun timeBetween(start: LocalDate?, end: LocalDate?): BooleanExpression? {
        return when {
            start != null && end != null -> user.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX))
            start != null -> user.createdAt.goe(start.atStartOfDay())
            end != null -> user.createdAt.loe(end.atTime(LocalTime.MAX))
            else -> null
        }
    }
}
