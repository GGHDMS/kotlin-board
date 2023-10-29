package project.kotlin_board.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 refresh response")
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)
