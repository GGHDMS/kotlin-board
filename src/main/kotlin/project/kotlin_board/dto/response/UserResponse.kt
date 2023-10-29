package project.kotlin_board.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import project.kotlin_board.model.Role

@Schema(description = "유저 response")
data class UserResponse(
    val email: String,
    val username: String,
    val role: Role,
)
