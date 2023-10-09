package project.kotlin_board.dto.response

import project.kotlin_board.model.Role

data class SignInResponse(
    val email: String,
    val username: String,
    val role: Role,
    val accessToken : String,
    val refreshToken : String
)

