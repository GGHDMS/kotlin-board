package project.kotlin_board.model.repository.querydsl

import project.kotlin_board.dto.response.UserWithTimeResponse
import java.time.LocalDate

interface UserRepositoryCustom {

    fun findUserListByQueryDsl(
        username: String?,
        email: String?,
        createdAtStart: LocalDate?,
        createdAtEnd: LocalDate?,
        updatedAtStart: LocalDate?,
        updatedAtEnd: LocalDate?,
    ): List<UserWithTimeResponse>
}
