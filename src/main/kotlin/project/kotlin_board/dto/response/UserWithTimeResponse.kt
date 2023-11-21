package project.kotlin_board.dto.response

import project.kotlin_board.model.Role
import java.time.LocalDateTime

data class UserWithTimeResponse(
    val id: Long,
    val email: String,
    val username: String,
    val role: Role,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
