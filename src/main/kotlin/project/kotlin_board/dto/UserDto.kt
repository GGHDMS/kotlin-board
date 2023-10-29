package project.kotlin_board.dto

import project.kotlin_board.model.Role

data class UserDto(
    val id: Long,
    val email: String,
    val username: String,
    val role: Role,
)
