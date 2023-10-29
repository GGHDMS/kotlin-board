package project.kotlin_board.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import project.kotlin_board.model.Role

@Schema(description = "로그인 response")
data class SignInResponse(
    val email: String,
    val username: String,
    val role: Role,
    val accessToken: String,
    val refreshToken: String,
)
